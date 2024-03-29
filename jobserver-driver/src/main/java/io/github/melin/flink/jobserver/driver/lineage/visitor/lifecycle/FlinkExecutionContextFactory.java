package io.github.melin.flink.jobserver.driver.lineage.visitor.lifecycle;

import java.util.List;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.dag.Transformation;

public class FlinkExecutionContextFactory {

    public static FlinkExecutionContext getContext(
            JobID jobId, List<Transformation<?>> transformations) {
        return new FlinkExecutionContext(jobId, transformations);
    }
}
