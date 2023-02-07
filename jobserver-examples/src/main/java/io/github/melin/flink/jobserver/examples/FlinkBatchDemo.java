package io.github.melin.flink.jobserver.examples;

import io.github.melin.flink.jobserver.api.FlinkJob;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class FlinkBatchDemo implements FlinkJob {

    @Override
    public void runJob(StreamExecutionEnvironment env,
                       StreamTableEnvironment tableEnv,
                       String[] args) throws Exception {

        tableEnv.executeSql("select * from hive_catalog.bigdata.test_demo_test1").print();
        System.out.println("hello world==================");
    }
}
