package io.github.melin.flink.jobserver.monitor.task;

import io.github.melin.flink.jobserver.core.entity.FlinkDriver;
import io.github.melin.flink.jobserver.core.service.FlinkDriverService;
import io.github.melin.flink.jobserver.support.YarnClientService;
import io.github.melin.flink.jobserver.support.leader.LeaderTypeEnum;
import io.github.melin.flink.jobserver.support.leader.RedisLeaderElection;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * huaixin 2022/3/19 12:48 PM
 */
@Service
public class UpdateDriverResourceTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger("serverMinitor");

    @Autowired
    private RedisLeaderElection redisLeaderElection;

    @Autowired
    private FlinkDriverService driverService;

    @Autowired
    private YarnClientService yarnClientService;

    @Override
    public void run() {
        if (!redisLeaderElection.checkLeader(LeaderTypeEnum.DRIVER_POOL_MONITOR)) {
            return;
        }

        List<FlinkDriver> drivers = driverService.findAllEntity();
        drivers.forEach(driver -> {
            try {
                String applicationId = driver.getApplicationId();
                String clusterCode = driver.getClusterCode();
                if (StringUtils.isNotBlank(applicationId)) {
                    ApplicationReport report = yarnClientService.getYarnApplicationReport(clusterCode, applicationId);
                    Resource resource = report.getApplicationResourceUsageReport().getNeededResources();

                    if (resource != null) {
                        driver.setServerCores(resource.getVirtualCores());
                        driver.setServerMemory(resource.getMemorySize());
                        driverService.updateEntity(driver);
                    }
                }
            } catch (Throwable e) {
                LOG.error("update driver {} resource failure: {}", driver.getApplicationId(), e.getMessage());
            }
        });
    }
}
