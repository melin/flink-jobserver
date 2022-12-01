package io.github.melin.flink.jobserver.deployment;

import com.google.common.collect.Lists;
import io.github.melin.flink.jobserver.core.entity.FlinkDriver;
import io.github.melin.flink.jobserver.core.enums.DriverStatus;
import io.github.melin.flink.jobserver.core.enums.RuntimeMode;
import io.github.melin.flink.jobserver.core.exception.ResouceLimitException;
import io.github.melin.flink.jobserver.core.exception.FlinkJobException;
import io.github.melin.flink.jobserver.core.service.FlinkDriverService;
import io.github.melin.flink.jobserver.deployment.dto.JobInstanceInfo;
import io.github.melin.flink.jobserver.support.ClusterConfig;
import io.github.melin.flink.jobserver.support.ClusterManager;
import io.github.melin.flink.jobserver.support.leader.RedisLeaderElection;
import io.github.melin.flink.jobserver.core.entity.Cluster;
import io.github.melin.flink.jobserver.web.controller.DriverController;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.cli.ApplicationDeployer;
import org.apache.flink.client.deployment.ClusterClientServiceLoader;
import org.apache.flink.client.deployment.DefaultClusterClientServiceLoader;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.deployment.application.cli.ApplicationClusterDeployer;
import org.apache.flink.configuration.*;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.github.melin.flink.jobserver.FlinkJobServerConf.*;
import static org.apache.flink.yarn.configuration.YarnConfigOptions.*;

/**
 * 参考 Flink CliFrontend 启动提交FLink Driver
 */
@Service
public class YarnApplicationDriverDeployer extends AbstractDriverDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(YarnApplicationDriverDeployer.class);

    private final ClusterClientServiceLoader clusterClientServiceLoader;

    @Autowired
    protected ClusterManager clusterManager;

    @Autowired
    private ClusterConfig clusterConfig;

    @Autowired
    protected FlinkDriverService driverService;

    @Autowired
    private RedisLeaderElection redisLeaderElection;

    public YarnApplicationDriverDeployer() {
        this.clusterClientServiceLoader = new DefaultClusterClientServiceLoader();
    }

    public void buildJobServer(Cluster cluster, RuntimeMode runtimeMode) {
        Long driverId = null;
        String clusterCode = cluster.getCode();
        try {
            //未分配到server的请求重新申请server
            checkLocalAvailableMemory();
            checkMaxDriverCount(clusterCode);
            clusterManager.checkYarnResourceLimit(clusterCode);

            JobInstanceInfo jobInstanceInfo = new JobInstanceInfo();
            jobInstanceInfo.setClusterCode(clusterCode);
            String yarnQueue = clusterConfig.getValue(clusterCode, JOBSERVER_DRIVER_YARN_QUEUE_NAME);
            jobInstanceInfo.setYarnQueue(yarnQueue);

            driverId = initFlinkDriver(clusterCode, true);
            LOG.info("预启动 driver Id: {}", driverId);

            long appSubmitTime = System.currentTimeMillis();
            String applicationId = startApplication(jobInstanceInfo, driverId, runtimeMode);

            Long times = (System.currentTimeMillis() - appSubmitTime) / 1000L;
            LOG.info("start share jobserver: {}, times: {}s", applicationId, times);

            if (StringUtils.isNotBlank(applicationId)) {
                FlinkDriver driver = driverService.getEntity(driverId);
                if (driver != null) {
                    driver.setApplicationId(applicationId);
                    driverService.updateEntity(driver);
                }
            }

            DriverController.flinkLauncherFailedMsg = "";
        } catch (Throwable e) {
            LOG.info("启动jobserver 失败: " + e.getMessage(), e);
            if (driverId != null) {
                LOG.info("delete driverId: {}", driverId);
                driverService.deleteEntity(driverId);
            }

            DriverController.flinkLauncherFailedMsg = "启动jobserver 失败: " + e.getMessage();

            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (Exception ignored) {}
        }
    }

    @Override
    protected String startApplication(JobInstanceInfo jobInstanceInfo, Long driverId, RuntimeMode runtimeMode) throws Exception {
        Configuration flinkConfig = buildFlinkConfig(jobInstanceInfo);
        flinkConfig.setString(DeploymentOptions.TARGET, YarnDeploymentTarget.APPLICATION.getName());

        final String conf = Base64.getEncoder().encodeToString("{}".getBytes(StandardCharsets.UTF_8));
        String clusterCode = jobInstanceInfo.getClusterCode();
        List<String> programArgs = Lists.newArrayList("-j", String.valueOf(driverId), "-conf", conf,
                "-c", clusterCode, "-mode", runtimeMode.getValue());
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
     * 初始化jobserver实例
     */
    protected Long initFlinkDriver(String clusterCode, boolean shareDriver) {
        Long driverId;
        try {
            FlinkDriver driver = FlinkDriver.buildFlinkDriver(clusterCode, shareDriver);
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
}