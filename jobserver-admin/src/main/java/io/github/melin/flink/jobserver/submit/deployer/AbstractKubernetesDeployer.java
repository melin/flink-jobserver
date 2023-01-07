package io.github.melin.flink.jobserver.submit.deployer;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PushImageCmd;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.google.common.collect.Sets;
import io.github.melin.flink.jobserver.submit.support.FlinkDockerfileTemplate;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static io.github.melin.flink.jobserver.FlinkJobServerConf.*;

abstract public class AbstractKubernetesDeployer extends AbstractDriverDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractKubernetesDeployer.class);

    public static final String LOCAL_DOCKER_WS_DIR = FileUtils.getUserDirectory() + "/tmp/docker_ws";

    private static final DefaultDockerClientConfig DOCKER_CLIENT_CONF =
            DefaultDockerClientConfig.createDefaultConfigBuilder().build();

    protected void buildDockerImage(String clusterCode, String applicationId, String clusterConfDir) throws IOException {
        String buildWorkspace = initBuildWorkspace(applicationId);

        FlinkDockerfileTemplate flinkDockerfileTemplate =
                new FlinkDockerfileTemplate(clusterCode, clusterConfig, clusterConfDir, buildWorkspace);
        String baseImageTag = flinkDockerfileTemplate.getBaseImageTag();

        DockerClient dockerClient = newDockerClient(clusterCode);
        pullFlinkBaseImage(dockerClient, baseImageTag);

        String pushImageTag = "flink-jobserver-job-" + applicationId;
        String registerAddress = clusterConfig.getValue(clusterCode, JOBSERVER_DOCKER_REGISTER_ADDRESS);
        if (StringUtils.isNotBlank(registerAddress)) {
            pushImageTag = registerAddress + "/" + pushImageTag;
        }
        pushImageTag = pushImageTag.toLowerCase();

        File dockerfile = flinkDockerfileTemplate.writeDockerfile();
        buildFlinkImage(dockerClient, dockerfile, pushImageTag, buildWorkspace);
        pushFlinkImage(clusterCode, dockerClient, pushImageTag);
    }

    private String initBuildWorkspace(String applicationId) throws IOException {
        String buildWorkspace = LOCAL_DOCKER_WS_DIR + "/" + applicationId;
        File file = new File(buildWorkspace);
        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
        FileUtils.forceMkdir(file);
        LOG.info("recreate building workspace: " + buildWorkspace);
        return buildWorkspace;
    }

    private void pullFlinkBaseImage(DockerClient dockerClient, String baseImageTag) {
        try {
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(baseImageTag);
            pullImageCmd.start().awaitCompletion();
            LOG.info("already pulled docker image from remote register, imageTag=" + baseImageTag);
        } catch (Exception e) {
            throw new RuntimeException("pull docker image failed, imageTag=" + baseImageTag, e);
        }
    }

    private void buildFlinkImage(DockerClient dockerClient, File dockerfile, String pushImageTag, String buildWorkspace) {
        try {
            BuildImageCmd buildImageCmd = dockerClient.buildImageCmd()
                    .withBaseDirectory(new File(buildWorkspace))
                    .withDockerfile(dockerfile)
                    .withTags(Sets.newHashSet(pushImageTag));

            BuildImageResultCallback buildCmdCallback = buildImageCmd.start();
            String imageId = buildCmdCallback.awaitImageId();
            LOG.info("built docker image, imageId=" + imageId + ", imageTag=" + pushImageTag);
        } catch (Exception e) {
            throw new RuntimeException("build docker image failed. tag=" + pushImageTag, e);
        }
    }

    private void pushFlinkImage(String clusterCode, DockerClient dockerClient, String pushImageTag) {
        try {
            String registerAddress = clusterConfig.getValue(clusterCode, JOBSERVER_DOCKER_REGISTER_ADDRESS);
            String registerUsername = clusterConfig.getValue(clusterCode, JOBSERVER_DOCKER_REGISTER_USERNAME);
            String registerPassword = clusterConfig.getValue(clusterCode, JOBSERVER_DOCKER_REGISTER_PASSWORD);
            AuthConfig authConf = new AuthConfig()
                    .withRegistryAddress(registerAddress)
                    .withUsername(registerUsername)
                    .withPassword(registerPassword);

            PushImageCmd pushCmd = dockerClient
                    .pushImageCmd(pushImageTag)
                    .withAuthConfig(authConf);

            ResultCallback.Adapter<PushResponseItem> pushCmdCallback = pushCmd.start();
            pushCmdCallback.awaitCompletion();
            LOG.info("already pushed docker image, imageTag=" + pushImageTag);
        } catch (Exception e) {
            throw new RuntimeException("push docker image failed. tag=" + pushImageTag, e);
        }
    }

    /**
     * get new DockerClient instance
     */
    public DockerClient newDockerClient(String clusterCode) {
        int maxConnections = clusterConfig.getInt(clusterCode, JOBSERVER_DOCKER_MAX_CONNECTIONS);
        long connectionTimeoutSec = clusterConfig.getLong(clusterCode, JOBSERVER_DOCKER_CONNECTION_TIMEOUT_SEC);
        long responseTimeoutSec = clusterConfig.getLong(clusterCode, JOBSERVER_DOCKER_RESPONSE_TIMEOUT_SEC);

        ApacheDockerHttpClient.Builder dockerHttpClientBuilder = new ApacheDockerHttpClient.Builder()
                .dockerHost(DOCKER_CLIENT_CONF.getDockerHost())
                .sslConfig(DOCKER_CLIENT_CONF.getSSLConfig())
                .maxConnections(maxConnections)
                .connectionTimeout(Duration.ofSeconds(connectionTimeoutSec))
                .responseTimeout(Duration.ofSeconds(responseTimeoutSec));

        return DockerClientImpl.getInstance(DOCKER_CLIENT_CONF, dockerHttpClientBuilder.build());
    }
}
