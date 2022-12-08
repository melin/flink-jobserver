package io.github.melin.flink.jobserver.driver;

import com.gitee.melin.bee.util.NetUtils;
import io.github.melin.flink.jobserver.core.dto.InstanceDto;
import io.github.melin.flink.jobserver.core.entity.ApplicationDriver;
import io.github.melin.flink.jobserver.core.enums.DriverStatus;
import io.github.melin.flink.jobserver.core.enums.RuntimeMode;
import io.github.melin.flink.jobserver.core.service.ApplicationDriverService;
import io.github.melin.flink.jobserver.driver.model.DriverParam;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class FlinkDriverContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlinkDriverContext.class);

    private volatile DriverStatus status = DriverStatus.IDLE;

    /**
     * 是否用户终止作业
     */
    private boolean userStopTask = false;

    @Autowired
    private ServerPortService serverPortService;

    @Autowired
    private ApplicationDriverService driverService;

    public void initFlinkDriver(DriverParam driverParam) {
        Long driverId = driverParam.getDriverId();
        ApplicationDriver driver = driverService.getEntity(driverId);
        if (driver == null) {
            throw new RuntimeException("No driver Id: " + driverId);
        }
        driver.setServerIp(NetUtils.getLocalHost());
        driver.setServerPort(serverPortService.getPort());
        driver.setStatus(DriverStatus.IDLE);
        if (driverParam.getRuntimeMode() == RuntimeExecutionMode.BATCH) {
            driver.setRuntimeMode(RuntimeMode.BATCH);
        } else if (driverParam.getRuntimeMode() == RuntimeExecutionMode.STREAMING) {
            driver.setRuntimeMode(RuntimeMode.STREAMING);
        }
        driver.setCreater("admin");

        Instant nowDate = Instant.now();
        driver.setGmtCreated(nowDate);
        driver.setGmtModified(nowDate);
        driverService.updateEntity(driver);
        LOGGER.info("driver status idle");
    }

    public void startDriver() {
        LOGGER.info("startQueySparkStageLog");
        this.setUserStopTask(false);
        status = DriverStatus.RUNNING;
        //logThread.startQueySparkStageLog();
    }

    public void stopDriver(InstanceDto instanceDto) {
        LOGGER.info("stopQueySparkStageLog");
        //logThread.stopQueySparkStageLog();

        ApplicationDriver driver = driverService.getEntity(instanceDto.getDriverId());
        LOGGER.info("driver {} run task finished，update status idle", driver.getApplicationId());

        Instant nowDate = Instant.now();
        driver.setGmtModified(nowDate);
        status = DriverStatus.IDLE;
        driver.setStatus(status);
        driverService.updateEntity(driver);
    }

    public DriverStatus getStatus() {
        return status;
    }

    public boolean isUserStopTask() {
        return userStopTask;
    }

    public void setUserStopTask(boolean userStopTask) {
        this.userStopTask = userStopTask;
    }
}
