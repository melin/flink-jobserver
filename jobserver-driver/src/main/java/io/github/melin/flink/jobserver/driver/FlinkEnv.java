package io.github.melin.flink.jobserver.driver;

import io.github.melin.flink.jobserver.api.FlinkJobServerException;
import jdk.jpackage.internal.Log;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.CountDownLatch;

public class FlinkEnv {
    private static final Logger LOG = LoggerFactory.getLogger(FlinkEnv.class);

    private static StreamTableEnvironment tableEnvironment;

    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void init(Configuration flinkConf, boolean kerberosEnabled, boolean hiveEnabled) throws Exception {
        if (tableEnvironment == null) {
            if (kerberosEnabled) {
                UserGroupInformation.getLoginUser().doAs((PrivilegedExceptionAction<Object>) () -> {
                    initSpark(flinkConf, hiveEnabled);
                    return null;
                });
            } else {
                initSpark(flinkConf, hiveEnabled);
            }
        }
    }

    private static void initSpark(Configuration flinkConf, boolean hiveEnabled) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(flinkConf);
        tableEnvironment = StreamTableEnvironment.create(env);
        LOG.info("StreamTableEnvironment inited");
    }

    public static String getApplicationId() {
        // applicationId 配置项
        ConfigOption<String> applicationId = ConfigOptions.key("yarn.application.id")
                .stringType()
                .noDefaultValue();
        try {
            Field configurationField = StreamExecutionEnvironment.class.getDeclaredField("configuration");
            if (!configurationField.isAccessible()) {
                configurationField.setAccessible(true);
            }
            Configuration configuration = (Configuration) configurationField.get(tableEnvironment);

            // 从配置中获取applicationId
            String appId = configuration.get(applicationId);
            Log.info("flink applicationId: " + appId);
            return appId;
        } catch (Exception e) {
            throw new FlinkJobServerException("get yarn appId failed: " + e.getMessage(), e);
        }
    }

    public static void waitDriver() throws InterruptedException {
        FlinkEnv.countDownLatch.await();
    }

}
