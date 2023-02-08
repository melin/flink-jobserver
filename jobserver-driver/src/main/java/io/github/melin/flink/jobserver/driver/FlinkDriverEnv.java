package io.github.melin.flink.jobserver.driver;

import io.github.melin.flink.jobserver.core.entity.Cluster;
import io.github.melin.flink.jobserver.driver.lineage.LineageFlinkJobListener;
import io.github.melin.flink.jobserver.driver.model.DriverParam;
import org.apache.calcite.rel.metadata.JaninoRelMetadataProvider;
import org.apache.calcite.rel.metadata.RelMetadataQueryBase;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.Catalog;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hudi.table.catalog.HoodieHiveCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.CountDownLatch;

public class FlinkDriverEnv {
    private static final Logger LOG = LoggerFactory.getLogger(FlinkDriverEnv.class);

    private static StreamExecutionEnvironment streamExecutionEnvironment;

    private static StreamTableEnvironment tableEnvironment;

    private static JaninoRelMetadataProvider metadataProvider;

    private static Configuration flinkConfig;

    public static volatile String applicationId;

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
        streamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment(flinkConf);
        streamExecutionEnvironment.setRuntimeMode(driverParam.getRuntimeMode());
        streamExecutionEnvironment.registerJobListener(new LineageFlinkJobListener(streamExecutionEnvironment));
        tableEnvironment = StreamTableEnvironment.create(streamExecutionEnvironment);

        flinkConfig = (Configuration) streamExecutionEnvironment.getConfiguration();
        applicationId = flinkConfig.get(HighAvailabilityOptions.HA_CLUSTER_ID);
        LOG.info("flink runtime mode: {}, applicationId: {}", driverParam.getRuntimeMode(), applicationId);

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

                Catalog catalog = new HiveCatalog("flink_hive", "default", hiveConf, null);
                tableEnvironment.registerCatalog("flink_hive", catalog);
                LOG.info("register hive catalog");
                tableEnvironment.useCatalog("flink_hive");
                tableEnvironment.getConfig().setSqlDialect(SqlDialect.HIVE);
                metadataProvider = RelMetadataQueryBase.THREAD_PROVIDERS.get();
                LOG.info("metadata {}", metadataProvider);

                try {
                    Catalog hudiCatalog = new HoodieHiveCatalog("flink_hudi", flinkConfig, hiveConf, false);
                    tableEnvironment.registerCatalog("flink_hudi", hudiCatalog);
                    LOG.info("register hudi catalog");
                } catch (Throwable e) {
                    LOG.warn("hudi-flink[version]-bundle-[version].jar not exist");
                }
            }
        }
        LOG.info("StreamTableEnvironment inited");
    }

    public static void waitDriver() throws InterruptedException {
        FlinkDriverEnv.countDownLatch.await();
    }

    public static StreamTableEnvironment getTableEnvironment() {
        return tableEnvironment;
    }

    public static StreamExecutionEnvironment getStreamExecutionEnvironment() {
        return streamExecutionEnvironment;
    }

    public static JaninoRelMetadataProvider getMetadataProvider() {
        return metadataProvider;
    }

    public static Configuration getFlinkConfig() {
        return flinkConfig;
    }
}
