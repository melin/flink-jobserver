package io.github.melin.flink.jobserver.api;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public interface FlinkJob {

    /**
     * tableEnv 已经设置 RuntimeMode
     * 如果流任务 RuntimeMode = RuntimeExecutionMode.STREAMING
     * 如果批任务 RuntimeMode = RuntimeExecutionMode.BATCH
     *
     * @param env StreamExecutionEnvironment
     * @param tableEnv StreamTableEnvironment
     * @param args 输入参数
     * @throws Exception
     */
    void runJob(StreamExecutionEnvironment env,
                StreamTableEnvironment tableEnv,
                String[] args) throws Exception;
}
