package io.github.melin.flink.jobserver.driver;

import io.github.melin.flink.jobserver.api.FlinkJobServerException;
import io.github.melin.flink.jobserver.core.entity.Cluster;
import io.github.melin.flink.jobserver.driver.model.DriverParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.Catalog;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.CountDownLatch;

public class FlinkDriverEnv {
    private static final Logger LOG = LoggerFactory.getLogger(FlinkDriverEnv.class);

    private static StreamTableEnvironment tableEnvironment;

    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void init(Configuration flinkConf, Cluster cluster, DriverParam driverParam) throws Exception {

        if (tableEnvironment == null) {
            if (driverParam.isKerberosEnabled()) {
                UserGroupInformation.getLoginUser().doAs((PrivilegedExceptionAction<Object>) () -> {
                    initFlink(flinkConf, cluster, driverParam);
                    return null;
                });
            } else {
                initFlink(flinkConf, cluster, driverParam);
            }
        }
    }

    private static void initFlink(Configuration flinkConf, Cluster cluster, DriverParam driverParam) {

        StreamExecutionEnvironment env = new StreamExecutionEnvironment(flinkConf);
        env.setRuntimeMode(driverParam.getRuntimeMode());
        tableEnvironment = StreamTableEnvironment.create(env);
        LOG.info("flink runtime mode: {}", driverParam.getRuntimeMode());

        LOG.info("create hive catalog: {}", driverParam.isHiveEnable());
        if (driverParam.isHiveEnable()) {
            org.apache.hadoop.conf.Configuration hadoopConf = new org.apache.hadoop.conf.Configuration();
            if (StringUtils.isNotBlank(cluster.getCoreConfig())) {
                hadoopConf.addResource(new ByteArrayInputStream(cluster.getCoreConfig().getBytes(StandardCharsets.UTF_8)));
            }
            if (StringUtils.isNotBlank(cluster.getHdfsConfig())) {
                hadoopConf.addResource(new ByteArrayInputStream(cluster.getHdfsConfig().getBytes(StandardCharsets.UTF_8)));
            }
            if (StringUtils.isNotBlank(cluster.getYarnConfig())) {
                hadoopConf.addResource(new ByteArrayInputStream(cluster.getYarnConfig().getBytes(StandardCharsets.UTF_8)));
            }

            if (StringUtils.isNotBlank(cluster.getHiveConfig())) {
                HiveConf.setHiveSiteLocation(null);
                HiveConf.setLoadMetastoreConfig(false);
                HiveConf.setLoadHiveServer2Config(false);
                HiveConf hiveConf = new HiveConf(hadoopConf, HiveConf.class);
                hiveConf.addResource(new ByteArrayInputStream(cluster.getHiveConfig().getBytes(StandardCharsets.UTF_8)));

                Catalog catalog = new HiveCatalog("hive_catalog", "default", hiveConf, null);
                tableEnvironment.registerCatalog("hive_catalog", catalog);
                tableEnvironment.useCatalog("hive_catalog");
            }
        }
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
        FlinkDriverEnv.countDownLatch.await();
    }

}
