package io.github.melin.flink.jobserver.driver.lineage;

import io.github.melin.flink.jobserver.driver.lineage.visitor.lifecycle.FlinkExecutionContext;
import io.github.melin.flink.jobserver.driver.lineage.visitor.lifecycle.FlinkExecutionContextFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.core.execution.DetachedJobExecutionResult;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.core.execution.JobListener;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Flink Listener registered within Flink Application which gets notified when application is
 * submitted and executed.
 */
@Slf4j
public class LineageFlinkJobListener implements JobListener {

    private final StreamExecutionEnvironment executionEnvironment;

    private final Map<JobID, FlinkExecutionContext> jobContexts = new HashMap<>();

    public LineageFlinkJobListener(
            StreamExecutionEnvironment executionEnvironment) {
        this.executionEnvironment = executionEnvironment;
        makeTransformationsArchivedList(executionEnvironment);
    }

    @Override
    public void onJobSubmitted(@Nullable JobClient jobClient, @Nullable Throwable throwable) {
        if (jobClient == null) {
            return;
        }

        try {
            start(jobClient);
        } catch (Exception | NoClassDefFoundError | NoSuchFieldError e) {
            log.error("Failed to notify OpenLineage about start", e);
        }
    }

    void start(JobClient jobClient) {
        Field transformationsField =
                FieldUtils.getField(StreamExecutionEnvironment.class, "transformations", true);
        try {
            List<Transformation<?>> transformations =
                    ((ArchivedList<Transformation<?>>) transformationsField.get(executionEnvironment))
                            .getValue();

            FlinkExecutionContext context =
                    FlinkExecutionContextFactory.getContext(jobClient.getJobID(), transformations);

            jobContexts.put(jobClient.getJobID(), context);
            log.info("Job submitted : {}", transformations.size());

            log.info("LineageContinousJobTracker is starting");
            context.onJobCheckpoint();
        } catch (IllegalAccessException e) {
            log.error("Can't access the field. ", e);
        }
    }

    @Override
    public void onJobExecuted(
            @Nullable JobExecutionResult jobExecutionResult, @Nullable Throwable throwable) {
        try {
            finish(jobExecutionResult, throwable);
        } catch (Exception | NoClassDefFoundError | NoSuchFieldError e) {
            log.error("Failed to notify OpenLineage about complete", e);
        }
    }

    void finish(@Nullable JobExecutionResult jobExecutionResult, @Nullable Throwable throwable) {
        if (jobExecutionResult instanceof DetachedJobExecutionResult) {
            jobContexts.remove(jobExecutionResult.getJobID());
            log.warn(
                    "Job running in detached mode. Set execution.attached to true if you want to emit completed events.");
            return;
        }

        if (jobExecutionResult != null) {
            jobContexts.remove(jobExecutionResult.getJobID()).onJobCompleted(jobExecutionResult);
        } else {
            // We don't have jobId when failed, so we need to assume that only existing context is that
            // job
            if (jobContexts.size() == 1) { // NOPMD
                Map.Entry<JobID, FlinkExecutionContext> entry =
                        jobContexts.entrySet().stream().findFirst().get();
                jobContexts.remove(entry.getKey()).onJobFailed(throwable);
            }
        }
    }

    private void makeTransformationsArchivedList(StreamExecutionEnvironment executionEnvironment) {
        try {
            Field transformations =
                    FieldUtils.getField(StreamExecutionEnvironment.class, "transformations", true);
            ArrayList<Transformation<?>> previousTransformationList =
                    (ArrayList<Transformation<?>>)
                            FieldUtils.readField(transformations, executionEnvironment, true);
            List<Transformation<?>> transformationList =
                    new ArchivedList<>(
                            Optional.ofNullable(previousTransformationList).orElse(new ArrayList<>()));
            FieldUtils.writeField(transformations, executionEnvironment, transformationList, true);
        } catch (IllegalAccessException e) {
            log.error("Failed to rewrite transformations");
        }
    }

    static class ArchivedList<T> extends ArrayList<T> {
        @Getter
        List<T> value;

        public ArchivedList(Collection<T> collection) {
            super(collection);
            value = new ArrayList<>(collection);
        }

        @Override
        public void clear() {
            value = new ArrayList<>(this);
            super.clear();
        }
    }
}
