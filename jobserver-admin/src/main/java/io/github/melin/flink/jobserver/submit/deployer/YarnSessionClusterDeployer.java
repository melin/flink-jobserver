package io.github.melin.flink.jobserver.submit.deployer;

import io.github.melin.flink.jobserver.core.entity.SessionCluster;
import io.github.melin.flink.jobserver.core.enums.SessionClusterStatus;
import io.github.melin.flink.jobserver.core.exception.FlinkJobException;
import io.github.melin.flink.jobserver.core.service.SessionClusterService;
import io.github.melin.flink.jobserver.submit.dto.DeploymentInfo;
import io.github.melin.flink.jobserver.support.YarnClientService;
import io.github.melin.flink.jobserver.web.controller.SessionClusterController;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.deployment.ClusterClientFactory;
import org.apache.flink.client.deployment.ClusterClientServiceLoader;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static org.apache.hadoop.yarn.api.records.YarnApplicationState.ACCEPTED;
import static org.apache.hadoop.yarn.api.records.YarnApplicationState.NEW;
import static org.apache.hadoop.yarn.api.records.YarnApplicationState.NEW_SAVING;
import static org.apache.hadoop.yarn.api.records.YarnApplicationState.RUNNING;
import static org.apache.hadoop.yarn.api.records.YarnApplicationState.SUBMITTED;

/**
 * 参考 Flink FlinkYarnSessionCli 启动提交FLink Driver
 */
@Service
public class YarnSessionClusterDeployer extends AbstractDriverDeployer<SessionCluster> {

    private static final Logger LOG = LoggerFactory.getLogger(YarnSessionClusterDeployer.class);

    private final ClusterClientServiceLoader clusterClientServiceLoader;

    @Autowired
    private YarnClientService yarnClientService;

    @Autowired
    protected SessionClusterService sessionClusterService;

    public YarnSessionClusterDeployer() {
        this.clusterClientServiceLoader = new DefaultClusterClientServiceLoader();
    }

    public void startSessionCluster(SessionCluster sessionCluster) {
        String clusterCode = sessionCluster.getClusterCode();
        try {
            LOG.info("启动 session cluster : {}", sessionCluster.getSessionName());

            DeploymentInfo<SessionCluster> deploymentInfo = DeploymentInfo.<SessionCluster>builder()
                    .setCluster(sessionCluster)
                    .setClusterCode(clusterCode)
                    .build();

            long appSubmitTime = System.currentTimeMillis();
            sessionCluster.setStatus(SessionClusterStatus.INIT);
            sessionClusterService.updateEntity(sessionCluster);
            String applicationId = startDriver(deploymentInfo, null);

            Long times = (System.currentTimeMillis() - appSubmitTime) / 1000L;
            LOG.info("start share jobserver: {}, times: {}s", applicationId, times);

            if (StringUtils.isNotBlank(applicationId)) {
                sessionCluster.setApplicationId(applicationId);
                sessionClusterService.updateEntity(sessionCluster);

                waitClusterStartup(clusterCode, applicationId, sessionCluster.getId());

                sessionCluster.setApplicationId(applicationId);
                sessionCluster.setStatus(SessionClusterStatus.RUNNING);
                sessionClusterService.updateEntity(sessionCluster);
            }

            SessionClusterController.flinkLauncherFailedMsg = "";
        } catch (Throwable e) {
            LOG.info("启动jobserver 失败: " + e.getMessage(), e);
            SessionClusterController.flinkLauncherFailedMsg = "启动 Session 失败: " + e.getMessage();

            sessionCluster.setStatus(SessionClusterStatus.CLOSED);
            sessionClusterService.updateEntity(sessionCluster);
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (Exception ignored) {}
        }
    }

    @Override
    protected String startDriver(DeploymentInfo<SessionCluster> deploymentInfo, Long driverId) throws Exception {
        return clusterManager.runSecured(deploymentInfo.getClusterCode(), () -> {
            Configuration effectiveConfiguration = buildFlinkConfig(deploymentInfo);
            effectiveConfiguration.set(DeploymentOptions.TARGET, YarnDeploymentTarget.SESSION.getName());
            addSessionConfig(effectiveConfiguration, deploymentInfo.getCluster());
            final ClusterClientFactory<ApplicationId> yarnClusterClientFactory =
                    clusterClientServiceLoader.getClusterClientFactory(effectiveConfiguration);

            final YarnClusterDescriptor yarnClusterDescriptor = (YarnClusterDescriptor)
                    yarnClusterClientFactory.createClusterDescriptor(effectiveConfiguration);

            try {
                final ClusterSpecification clusterSpecification =
                        yarnClusterClientFactory.getClusterSpecification(effectiveConfiguration);

                final ClusterClientProvider<ApplicationId> clusterClientProvider =
                        yarnClusterDescriptor.deploySessionCluster(clusterSpecification);
                ClusterClient<ApplicationId> clusterClient = clusterClientProvider.getClusterClient();

                // ------------------ ClusterClient deployed, handle connection details
                final ApplicationId yarnApplicationId = clusterClient.getClusterId();
                YarnClusterDescriptor.logDetachedClusterInformation(yarnApplicationId, LOG);
                return yarnApplicationId.toString();
            } finally {
                try {
                    yarnClusterDescriptor.close();
                } catch (Exception e) {
                    LOG.info("Could not properly close the yarn cluster descriptor.", e);
                }
            }
        });
    }

    @Override
    protected void waitClusterStartup(String clusterCode, String applicationId, Long clusterId) throws Exception {
        if (StringUtils.isBlank(applicationId)) {
            throw new IllegalStateException("applicationId can not blank");
        }

        // 等待 yarn application 提交中
        ApplicationReport report = yarnClientService.getYarnApplicationReport(clusterCode, applicationId);
        YarnApplicationState state = report.getYarnApplicationState();
        while (state == ACCEPTED || state == NEW || state == NEW_SAVING || state == SUBMITTED) {
            TimeUnit.SECONDS.sleep(1);
            report = yarnClientService.getYarnApplicationReport(clusterCode, applicationId);
            state = report.getYarnApplicationState();
        }

        // 等待 flink driver 启动中
        report = yarnClientService.getYarnApplicationReport(clusterCode, applicationId);
        state = report.getYarnApplicationState();
        SessionCluster cluster = sessionClusterService.querySessionClusterByAppId(applicationId);
        while (state == RUNNING && cluster.getStatus() == SessionClusterStatus.INIT) {
            TimeUnit.SECONDS.sleep(1);
            report = yarnClientService.getYarnApplicationReport(clusterCode, applicationId);
            state = report.getYarnApplicationState();
            cluster = sessionClusterService.querySessionClusterByAppId(applicationId);
        }

        if (state != RUNNING) {
            LOG.error("startup driver failed, {} state: {}", applicationId, state.name());
            String msg = "startup driver failed, " + applicationId + " state: " + state.name();
            throw new FlinkJobException(msg);
        }
    }
}
