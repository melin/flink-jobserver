package io.github.melin.flink.jobserver.monitor.task;

import io.github.melin.flink.jobserver.core.entity.ApplicationDriver;
import io.github.melin.flink.jobserver.core.service.ApplicationDriverService;
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
    private ApplicationDriverService driverService;

    @Autowired
    private YarnClientService yarnClientService;

    @Override
    public void run() {
        if (!redisLeaderElection.checkLeader(LeaderTypeEnum.DRIVER_POOL_MONITOR)) {
            return;
        }

        List<ApplicationDriver> drivers = driverService.findAllEntity();
        drivers.forEach(driver -> {
            String applicationId = driver.getApplicationId();
            try {
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
                boolean remoteException = yarnClientService.handleApplicationNotFoundException(e, applicationId);
                if (remoteException) {
                    LOG.error("update driver resource failure, yarn app {} not exists", applicationId);
                } else {
                    LOG.error("update driver resource failure: {}, yarn app {}", e.getMessage(), applicationId);
                }
            }
        });
    }
}
