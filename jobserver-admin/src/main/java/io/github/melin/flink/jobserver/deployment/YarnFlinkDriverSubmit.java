package io.github.melin.flink.jobserver.deployment;

import com.google.common.collect.Lists;
import io.github.melin.flink.jobserver.deployment.dto.JobInstanceInfo;
import io.github.melin.flink.jobserver.support.ClusterConfig;
import io.github.melin.flink.jobserver.support.ClusterManager;
import io.github.melin.flink.jobserver.util.FSUtils;
import io.github.melin.flink.jobserver.util.JobServerUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.cli.ApplicationDeployer;
import org.apache.flink.client.deployment.ClusterClientServiceLoader;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.deployment.application.cli.ApplicationClusterDeployer;
import org.apache.flink.configuration.*;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static io.github.melin.flink.jobserver.FlinkJobServerConf.*;
import static org.apache.flink.yarn.configuration.YarnConfigOptions.*;

/**
 * 参考 Flink CliFrontend 启动提交FLink Driver
 */
@Service
public class YarnFlinkDriverSubmit {

    private final ClusterClientServiceLoader clusterClientServiceLoader;

    @Autowired
    protected ClusterManager clusterManager;

    @Autowired
    private ClusterConfig clusterConfig;

    @Value("${spring.profiles.active}")
    protected String profiles;

    public YarnFlinkDriverSubmit() {
        this.clusterClientServiceLoader = new DefaultClusterClientServiceLoader();
    }

    protected void startApplication(JobInstanceInfo jobInstanceInfo, String clusterCode,
                                    Long driverId, String yarnQueue) throws Exception {

        final ApplicationDeployer deployer =
                new ApplicationClusterDeployer(clusterClientServiceLoader);

        final String confDir = clusterManager.loadYarnConfig(clusterCode);
        org.apache.hadoop.conf.Configuration hadoopConf = clusterManager.getHadoopConf(clusterCode);
        String defaultFS = hadoopConf.get("fs.defaultFS", "hdfs://dzcluster");
        Configuration flinkConfig = GlobalConfiguration.loadConfiguration(confDir);
        flinkConfig.setString(DeploymentOptions.TARGET, "yarn-application");

        //设置队列
        if (StringUtils.isNotBlank(yarnQueue)) {
            flinkConfig.setString(APPLICATION_QUEUE, yarnQueue);
        } else {
            yarnQueue = clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_YAEN_QUEUE_NAME);
            flinkConfig.setString(APPLICATION_QUEUE, yarnQueue);
        }
        String appName = JobServerUtils.appName(profiles);
        flinkConfig.setString(APPLICATION_NAME, appName);
        flinkConfig.setString(APPLICATION_TYPE, "spark-jobserver");

        String hadoopUserName = clusterConfig.getDriverHadoopUserName(clusterCode);
        System.setProperty("HADOOP_USER_NAME", hadoopUserName);
        flinkConfig.setString(CoreOptions.FLINK_JVM_OPTIONS, "-DHADOOP_USER_NAME=" + hadoopUserName);
        flinkConfig.setString(CoreOptions.FLINK_JM_JVM_OPTIONS, "-DHADOOP_USER_NAME=" + hadoopUserName);
        // Detached模式下，Flink Client创建完集群之后，可以退出命令行窗口，集群独立运行。Attached模式下，Flink Client创建完集群后，不能关闭命令行窗口，需要与集群之间维持连接
        flinkConfig.setBoolean(DeploymentOptions.ATTACHED, false);

        String driverHome = clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_HOME);
        String flinkVersion = clusterConfig.getValue(clusterCode, JOBSERVER_FLINK_VERSION);
        String flinkYarnJarsDir = driverHome + "/flink-" + flinkVersion;
        FSUtils.checkHdfsPathExist(hadoopConf, flinkYarnJarsDir);
        flinkConfig.set(YarnConfigOptions.PROVIDED_LIB_DIRS, Collections.singletonList(defaultFS + flinkYarnJarsDir));

        String driverJar = defaultFS + driverHome + "/" + clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_JAR_NAME);
        List<String> jobJars = Lists.newArrayList(driverJar);
        ConfigUtils.encodeCollectionToConfig(flinkConfig, PipelineOptions.JARS, jobJars, Object::toString);

        final String conf = Base64.getEncoder().encodeToString("{}".getBytes(StandardCharsets.UTF_8));
        List<String> programArgs = Lists.newArrayList("-j", String.valueOf(driverId), "-conf", conf);
        boolean hiveEnabled = clusterConfig.getBoolean(clusterCode, JOBSERVER_DRIVER_HIVE_ENABLED);
        if (hiveEnabled) {
            programArgs.add("-hive");
        }
        final ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration(
                programArgs.toArray(new String[0]), "io.github.melin.flink.jobserver.driver.FlinkDriverApp");
        deployer.run(flinkConfig, applicationConfiguration);
    }
}
