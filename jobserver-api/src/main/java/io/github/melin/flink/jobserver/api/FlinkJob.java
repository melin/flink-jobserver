package io.github.melin.flink.jobserver.api;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public interface FlinkJob {
    void runJob(StreamExecutionEnvironment streamExecutionEnvironment, String[] args) throws FlinkJobServerException;
}
