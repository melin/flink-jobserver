package io.github.melin.flink.jobserver.submit.deployer;

import com.google.common.collect.Lists;
import io.github.melin.flink.jobserver.core.entity.ApplicationDriver;
import io.github.melin.flink.jobserver.core.entity.Cluster;
import io.github.melin.flink.jobserver.core.enums.DriverStatus;
import io.github.melin.flink.jobserver.core.enums.RuntimeMode;
import io.github.melin.flink.jobserver.core.exception.FlinkJobException;
import io.github.melin.flink.jobserver.core.exception.ResouceLimitException;
import io.github.melin.flink.jobserver.core.service.ApplicationDriverService;
import io.github.melin.flink.jobserver.submit.dto.DriverDeploymentInfo;
import io.github.melin.flink.jobserver.support.ClusterManager;
import io.github.melin.flink.jobserver.support.YarnClientService;
import io.github.melin.flink.jobserver.support.leader.RedisLeaderElection;
import io.github.melin.flink.jobserver.web.controller.ApplicationDriverController;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.cli.ApplicationDeployer;
import org.apache.flink.client.deployment.ClusterClientServiceLoader;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.deployment.application.cli.ApplicationClusterDeployer;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.github.melin.flink.jobserver.FlinkJobServerConf.*;
import static org.apache.flink.yarn.configuration.YarnConfigOptions.APPLICATION_ID;
import static org.apache.hadoop.yarn.api.records.YarnApplicationState.*;

/**
 * ?????? Flink CliFrontend ????????????FLink Driver
 */
@Service
public class KubernetesApplicationDriverDeployer extends AbstractKubernetesDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(KubernetesApplicationDriverDeployer.class);

    private final ClusterClientServiceLoader clusterClientServiceLoader;

    @Autowired
    protected ClusterManager clusterManager;

    @Autowired
    protected ApplicationDriverService driverService;

    @Autowired
    private RedisLeaderElection redisLeaderElection;

    @Autowired
    private YarnClientService yarnClientService;

    public KubernetesApplicationDriverDeployer() {
        this.clusterClientServiceLoader = new DefaultClusterClientServiceLoader();
    }

    public void buildJobServer(Cluster cluster, RuntimeMode runtimeMode) {
        Long driverId = null;
        String clusterCode = cluster.getCode();
        try {
            //????????????server?????????????????????server
            checkLocalAvailableMemory();
            checkMaxDriverCount(clusterCode);
            clusterManager.checkYarnResourceLimit(clusterCode);

            String yarnQueue = clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_YARN_QUEUE_NAME);
            DriverDeploymentInfo deploymentInfo = DriverDeploymentInfo.builder()
                    .setClusterCode(clusterCode)
                    .setYarnQueue(yarnQueue)
                    .setRuntimeMode(runtimeMode)
                    .build();

            driverId = initFlinkDriver(clusterCode, true);
            LOG.info("????????? driver Id: {}", driverId);

            long appSubmitTime = System.currentTimeMillis();
            String applicationId = startDriver(deploymentInfo, driverId);

            Long times = (System.currentTimeMillis() - appSubmitTime) / 1000L;
            LOG.info("start share jobserver: {}, times: {}s", applicationId, times);

            if (StringUtils.isNotBlank(applicationId)) {
                ApplicationDriver driver = driverService.getEntity(driverId);
                if (driver != null) {
                    driver.setApplicationId(applicationId);
                    driverService.updateEntity(driver);
                }

                waitDriverStartup(clusterCode, applicationId);
            }

            ApplicationDriverController.flinkLauncherFailedMsg = "";
        } catch (Throwable e) {
            LOG.info("??????jobserver ??????: " + e.getMessage(), e);
            if (driverId != null) {
                LOG.info("delete driverId: {}", driverId);
                driverService.deleteEntity(driverId);
            }

            ApplicationDriverController.flinkLauncherFailedMsg = "??????jobserver ??????: " + e.getMessage();

            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (Exception ignored) {}
        }
    }

    @Override
    protected String startDriver(DriverDeploymentInfo deploymentInfo, Long driverId) throws Exception {
        Configuration flinkConfig = buildFlinkConfig(deploymentInfo);
        flinkConfig.setString(DeploymentOptions.TARGET, YarnDeploymentTarget.APPLICATION.getName());

        final String conf = Base64.getEncoder().encodeToString("{}".getBytes(StandardCharsets.UTF_8));
        final String clusterCode = deploymentInfo.getClusterCode();
        List<String> programArgs = Lists.newArrayList("-j", String.valueOf(driverId), "-conf", conf,
                "-c", clusterCode, "-mode", deploymentInfo.getRuntimeMode().getValue());
        boolean hiveEnabled = clusterConfig.getBoolean(clusterCode, JOBSERVER_DRIVER_HIVE_ENABLED);
        if (hiveEnabled) {
            programArgs.add("-hive");
        }

        final ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration(
                programArgs.toArray(new String[0]), "io.github.melin.flink.jobserver.driver.FlinkDriverServer");
        final ApplicationDeployer deployer = new ApplicationClusterDeployer(clusterClientServiceLoader);
        deployer.run(flinkConfig, applicationConfiguration);
        return flinkConfig.get(APPLICATION_ID);
    }

    /**
     * ?????????jobserver??????
     */
    protected Long initFlinkDriver(String clusterCode, boolean shareDriver) {
        Long driverId;
        try {
            ApplicationDriver driver = ApplicationDriver.buildApplicationDriver(clusterCode, shareDriver);
            String yarnQueue = clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_YARN_QUEUE_NAME);
            driver.setYarnQueue(yarnQueue);

            while (!redisLeaderElection.trylock()) {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            LOG.info("Get redis lock");

            long initDriverCount = driverService.queryCount("status", DriverStatus.INIT);
            long maxConcurrentSubmitCount = clusterConfig.getInt(clusterCode, JOBSERVER_SUBMIT_DRIVER_MAX_CONCURRENT_COUNT);
            if (initDriverCount > maxConcurrentSubmitCount) {
                String msg = "??????????????????jobserver??????: " + initDriverCount + ", ??????????????????: " + maxConcurrentSubmitCount
                        + ", ???????????????: jobserver.concurrent.submit.max.num";
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

        // ?????? yarn application ?????????
        ApplicationReport report = yarnClientService.getYarnApplicationReport(clusterCode, applicationId);
        YarnApplicationState state = report.getYarnApplicationState();
        while (state == ACCEPTED || state == NEW || state == NEW_SAVING || state == SUBMITTED) {
            TimeUnit.SECONDS.sleep(1);
            report = yarnClientService.getYarnApplicationReport(clusterCode, applicationId);
            state = report.getYarnApplicationState();
        }

        // ?????? flink driver ?????????
        report = yarnClientService.getYarnApplicationReport(clusterCode, applicationId);
        state = report.getYarnApplicationState();
        ApplicationDriver driver = driverService.queryDriverByAppId(applicationId);
        while (state == RUNNING && driver.getStatus() == DriverStatus.INIT) {
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
