package io.github.melin.flink.jobserver.examples;

import io.github.melin.flink.jobserver.api.FlinkJob;
import io.github.melin.flink.jobserver.api.LogUtils;
import org.apache.flink.hive.shaded.parquet.hadoop.ParquetFileReader;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class FlinkBatchDemo implements FlinkJob {

    @Override
    public void runJob(StreamExecutionEnvironment env,
                       StreamTableEnvironment tableEnv,
                       String[] args) throws Exception {

        LogUtils.info(ParquetFileReader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        tableEnv.executeSql("select * from flink_hudi.bigdata.test_hudi_demo").print();
        LogUtils.info("hello world ==================");
    }
}
