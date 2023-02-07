package io.github.melin.flink.jobserver.driver.lineage.visitor.lifecycle;

import io.github.melin.flink.jobserver.driver.lineage.SinkLineage;
import io.github.melin.flink.jobserver.driver.lineage.TransformationUtils;
import io.github.melin.flink.jobserver.driver.util.LogUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.dag.Transformation;

import java.util.List;
import java.util.UUID;

@Slf4j
public class FlinkExecutionContext implements ExecutionContext {

    @Getter
    private final JobID jobId;

    private final UUID runId;

    @Getter private final List<Transformation<?>> transformations;

    public FlinkExecutionContext(JobID jobId, List<Transformation<?>> transformations) {
        this.jobId = jobId;
        this.runId = UUID.randomUUID();
        this.transformations = transformations;
    }

    @Override
    public void onJobCheckpoint() {
        TransformationUtils converter = new TransformationUtils();
        List<SinkLineage> sinkLineages = converter.convertToVisitable(transformations);

        log.info("JobClient - jobId: {}", jobId);
        for (SinkLineage lineage : sinkLineages) {
            if (lineage.getSink() != null) {
                LogUtils.error("sink: " + lineage.getSink().getClass().getName());
            }

            if (lineage.getSources() != null) {
                lineage.getSources().forEach(source -> LogUtils.error("source: " + source.getClass().getName()));
            }
        }
    }

    @Override
    public void onJobCompleted(JobExecutionResult jobExecutionResult) {

    }

    @Override
    public void onJobFailed(Throwable failed) {
    }
}
