package io.github.melin.flink.jobserver.deployment;

import com.google.common.collect.Lists;
import org.apache.flink.client.cli.ApplicationDeployer;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.client.deployment.ClusterClientServiceLoader;
import org.apache.flink.client.deployment.ClusterDescriptor;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.deployment.application.cli.ApplicationClusterDeployer;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.*;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkException;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FlinkJobSubmitService {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkJobSubmitService.class);

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

        // list job
        // flinkConfiguration.set(YarnConfigOptions.APPLICATION_ID, ConverterUtils.toString(appId));
        final ClusterClientFactory<ApplicationId> clusterClientFactory =
                clusterClientServiceLoader.getClusterClientFactory(flinkConfig);
        final ApplicationId clusterId = clusterClientFactory.getClusterId(flinkConfig);
        if (clusterId == null) {
            throw new FlinkException(
                    "No cluster id was specified. Please specify a cluster to which you would like to connect.");
        }

        try (final ClusterDescriptor<ApplicationId> clusterDescriptor =
                     clusterClientFactory.createClusterDescriptor(flinkConfig)) {
            try (final ClusterClient<ApplicationId> clusterClient =
                         clusterDescriptor.retrieve(clusterId).getClusterClient()) {
                Collection<JobStatusMessage> jobDetails;
                try {
                    CompletableFuture<Collection<JobStatusMessage>> jobDetailsFuture =
                            clusterClient.listJobs();

                    jobDetails = jobDetailsFuture.get();
                } catch (Exception e) {
                    Throwable cause = ExceptionUtils.stripExecutionException(e);
                    throw new FlinkException("Failed to retrieve job list.", cause);
                }

                LOG.info("Successfully retrieved list of jobs");
            }
        }
    }
}
