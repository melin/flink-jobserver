package io.github.melin.flink.jobserver.deployment;

import io.github.melin.flink.jobserver.core.entity.Cluster;
import io.github.melin.flink.jobserver.core.entity.SessionCluster;
import io.github.melin.flink.jobserver.core.enums.DriverStatus;
import io.github.melin.flink.jobserver.core.enums.RuntimeMode;
import io.github.melin.flink.jobserver.core.enums.SessionClusterStatus;
import io.github.melin.flink.jobserver.core.exception.FlinkJobException;
import io.github.melin.flink.jobserver.core.exception.ResouceLimitException;
import io.github.melin.flink.jobserver.core.service.SessionClusterService;
import io.github.melin.flink.jobserver.deployment.dto.DriverDeploymentInfo;
import io.github.melin.flink.jobserver.support.ClusterConfig;
import io.github.melin.flink.jobserver.support.YarnClientService;
import io.github.melin.flink.jobserver.support.leader.RedisLeaderElection;
import io.github.melin.flink.jobserver.web.controller.ApplicationDriverController;
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

import static io.github.melin.flink.jobserver.FlinkJobServerConf.JOBSERVER_DRIVER_YARN_QUEUE_NAME;
import static io.github.melin.flink.jobserver.FlinkJobServerConf.JOBSERVER_SUBMIT_DRIVER_MAX_CONCURRENT_COUNT;
import static org.apache.hadoop.yarn.api.records.YarnApplicationState.*;

/**
 * 参考 Flink FlinkYarnSessionCli 启动提交FLink Driver
 */
@Service
public class KubenetesSessionDriverDeployer extends AbstractDriverDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(KubenetesSessionDriverDeployer.class);

    private final ClusterClientServiceLoader clusterClientServiceLoader;

    @Autowired
    private ClusterConfig clusterConfig;

    @Autowired
    private RedisLeaderElection redisLeaderElection;

    @Autowired
    private YarnClientService yarnClientService;

    @Autowired
    protected SessionClusterService driverService;

    public KubenetesSessionDriverDeployer() {
        this.clusterClientServiceLoader = new DefaultClusterClientServiceLoader();
    }

    public void buildJobServer(Cluster cluster, RuntimeMode runtimeMode, String sessionName) {
        Long driverId = null;
        String clusterCode = cluster.getCode();
        try {
            driverId = initFlinkDriver(clusterCode, sessionName);
            LOG.info("预启动 driver Id: {}", driverId);

            String yarnQueue = clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_YARN_QUEUE_NAME);
            DriverDeploymentInfo deploymentInfo = DriverDeploymentInfo.builder()
                    .setClusterCode(clusterCode)
                    .setYarnQueue(yarnQueue)
                    .setRuntimeMode(runtimeMode)
                    .build();

            long appSubmitTime = System.currentTimeMillis();
            String applicationId = startDriver(deploymentInfo, driverId);

            Long times = (System.currentTimeMillis() - appSubmitTime) / 1000L;
            LOG.info("start share jobserver: {}, times: {}s", applicationId, times);

            if (StringUtils.isNotBlank(applicationId)) {
                SessionCluster driver = driverService.getEntity(driverId);
                if (driver != null) {
                    driver.setApplicationId(applicationId);
                    driverService.updateEntity(driver);
                }

                waitDriverStartup(clusterCode, applicationId);
            }

            ApplicationDriverController.flinkLauncherFailedMsg = "";
        } catch (Throwable e) {
            LOG.info("启动jobserver 失败: " + e.getMessage(), e);
            if (driverId != null) {
                LOG.info("delete driverId: {}", driverId);
                driverService.deleteEntity(driverId);
            }

            ApplicationDriverController.flinkLauncherFailedMsg = "启动jobserver 失败: " + e.getMessage();

            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (Exception ignored) {}
        }
    }

    @Override
    protected String startDriver(DriverDeploymentInfo deploymentInfo, Long driverId) throws Exception {
        Configuration effectiveConfiguration = buildFlinkConfig(deploymentInfo, driverId);
        final ClusterClientFactory<ApplicationId> yarnClusterClientFactory =
                clusterClientServiceLoader.getClusterClientFactory(effectiveConfiguration);
        effectiveConfiguration.set(DeploymentOptions.TARGET, YarnDeploymentTarget.SESSION.getName());

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
    }

    /**
     * 初始化jobserver实例
     */
    protected Long initFlinkDriver(String clusterCode, String sessionName) {
        Long driverId;
        try {
            SessionCluster driver = SessionCluster.buildSessionDriver(clusterCode, sessionName);
            String yarnQueue = clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_YARN_QUEUE_NAME);
            driver.setYarnQueue(yarnQueue);

            while (!redisLeaderElection.trylock()) {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            LOG.info("Get redis lock");

            long initDriverCount = driverService.queryCount("status", DriverStatus.INIT);
            long maxConcurrentSubmitCount = clusterConfig.getInt(clusterCode, JOBSERVER_SUBMIT_DRIVER_MAX_CONCURRENT_COUNT);
            if (initDriverCount > maxConcurrentSubmitCount) {
                String msg = "当前正在提交jobserver数量: " + initDriverCount + ", 最大提交数量: " + maxConcurrentSubmitCount
                        + ", 可调整参数: jobserver.concurrent.submit.max.num";
                throw new ResouceLimitException(msg);
            }
            driverId = driverService.insertEntity(driver);
        } catch (FlinkJobException jobException) {
            throw jobException;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            redisLeaderElection.deletelock();
        }

        if (driverId == null) {
            throw new RuntimeException("Init Flink Driver Error");
        }
        return driverId;
    }

    @Override
    protected void waitDriverStartup(String clusterCode, String applicationId) throws Exception {
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
        SessionCluster driver = driverService.queryDriverByAppId(applicationId);
        while (state == RUNNING && driver.getStatus() == SessionClusterStatus.INIT) {
            TimeUnit.SECONDS.sleep(1);
            report = yarnClientService.getYarnApplicationReport(clusterCode, applicationId);
            state = report.getYarnApplicationState();
            driver = driverService.queryDriverByAppId(applicationId);
        }

        if (state != RUNNING) {
            LOG.error("startup driver failed, {} state: {}", applicationId, state.name());
            String msg = "startup driver failed, " + applicationId + " state: " + state.name();
            throw new FlinkJobException(msg);
        }
    }
}
