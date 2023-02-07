package io.github.melin.flink.jobserver.driver.lineage.visitor.lifecycle;

import org.apache.flink.api.common.JobExecutionResult;

public interface ExecutionContext {

    void onJobCheckpoint();

    void onJobCompleted(JobExecutionResult jobExecutionResult);

    void onJobFailed(Throwable failed);
}
