package io.github.melin.flink.jobserver.driver;

import io.github.melin.flink.jobserver.api.FlinkJobServerException;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
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
                    initFlink(flinkConf, hiveEnabled);
                    return null;
                });
            } else {
                initFlink(flinkConf, hiveEnabled);
            }
        }
    }

    private static void initFlink(Configuration flinkConf, boolean hiveEnabled) {
        flinkConf.set(DeploymentOptions.TARGET, YarnDeploymentTarget.SESSION.getName());
        StreamExecutionEnvironment env = new StreamExecutionEnvironment(flinkConf);
        tableEnvironment = StreamTableEnvironment.create(env);
        //@TODO 注册 hive catalog
        //tableEnvironment.useCatalog();
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
            LOG.info("flink applicationId: " + appId);
            return appId;
        } catch (Exception e) {
            throw new FlinkJobServerException("get yarn appId failed: " + e.getMessage(), e);
        }
    }

    public static void waitDriver() throws InterruptedException {
        FlinkEnv.countDownLatch.await();
    }

}
