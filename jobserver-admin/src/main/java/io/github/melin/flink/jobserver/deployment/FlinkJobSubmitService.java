package io.github.melin.flink.jobserver.deployment;

import com.google.common.collect.Lists;
import org.apache.flink.client.cli.ApplicationDeployer;
import org.apache.flink.client.cli.CliFrontendParser;
import org.apache.flink.client.deployment.ClusterClientServiceLoader;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.deployment.application.cli.ApplicationClusterDeployer;
import org.apache.flink.configuration.*;
import org.apache.flink.yarn.configuration.YarnConfigOptions;

import java.util.Collections;
import java.util.List;

import static org.apache.flink.yarn.configuration.YarnConfigOptions.PROVIDED_LIB_DIRS;

public class FlinkJobSubmitService {

    private final ClusterClientServiceLoader clusterClientServiceLoader;

    private String jarFilePath;

    public FlinkJobSubmitService() {
        this.clusterClientServiceLoader = new DefaultClusterClientServiceLoader();
    }

    protected void submitJob() throws Exception {
        final ApplicationDeployer deployer =
                new ApplicationClusterDeployer(clusterClientServiceLoader);

        // load the global configuration
        String configurationDirectory = "jobserver-admin/src/main/resources/";
        Configuration flinkConfig =
                GlobalConfiguration.loadConfiguration(configurationDirectory);
        flinkConfig.setString(DeploymentOptions.TARGET, "yarn-application");
        System.setProperty("HADOOP_USER_NAME", "devops");
        flinkConfig.setString(CoreOptions.FLINK_JVM_OPTIONS, "-DHADOOP_USER_NAME=devops");
        flinkConfig.setString(CoreOptions.FLINK_JM_JVM_OPTIONS, "-DHADOOP_USER_NAME=devops");
        flinkConfig.setBoolean(DeploymentOptions.ATTACHED, false);

        flinkConfig.set(YarnConfigOptions.PROVIDED_LIB_DIRS, Collections.singletonList("hdfs://newns:8020/user/superior/flink-jobserver/flink-1.15.1"));

        List<String> jobJars = Lists.newArrayList("hdfs://newns:8020/user/superior/flink-jobserver/flink-jobserver-driver-0.1.0.jar");
        ConfigUtils.encodeCollectionToConfig(
                flinkConfig, PipelineOptions.JARS, jobJars, Object::toString);

        ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.fromConfiguration(flinkConfig);
        deployer.run(flinkConfig, applicationConfiguration);
    }

    public static void main(String[] args) throws Exception {
        FlinkJobSubmitService submitService = new FlinkJobSubmitService();
        submitService.submitJob();
    }
}
