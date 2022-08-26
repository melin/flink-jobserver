package io.github.melin.flink.jobserver.examples;

import io.github.melin.flink.jobserver.api.FlinkJob;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class FlinkBatchDemo implements FlinkJob {

    @Override
    public void runJob(StreamExecutionEnvironment env,
                       StreamTableEnvironment tableEnv,
                       String[] args) throws Exception {

        tableEnv.executeSql(
                // define the aggregation
                "SELECT word, SUM(frequency) AS `count`\n"
                        // read from an artificial fixed-size table with rows and columns
                        + "FROM (\n"
                        + "  VALUES ('Hello', 1), ('Ciao', 1), ('Hello', 2)\n"
                        + ")\n"
                        // name the table and its columns
                        + "AS WordTable(word, frequency)\n"
                        // group for aggregation
                        + "GROUP BY word")
                .print();
    }
}
