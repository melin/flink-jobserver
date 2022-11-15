package io.github.melin.flink.jobserver.monitor.task;

import com.gitee.melin.bee.core.support.Result;
import io.github.melin.flink.jobserver.core.entity.FlinkDriver;
import io.github.melin.flink.jobserver.core.service.FlinkDriverService;
import io.github.melin.flink.jobserver.support.leader.LeaderTypeEnum;
import io.github.melin.flink.jobserver.support.leader.RedisLeaderElection;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

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
    private RestTemplate restTemplate;

    @Override
    public void run() {
        if (!redisLeaderElection.checkLeader(LeaderTypeEnum.DRIVER_POOL_MONITOR)) {
            return;
        }

        List<FlinkDriver> drivers = driverService.findAllEntity();
        drivers.forEach(driver -> {
            String flinkDriverUrl = driver.getFlinkDriverUrl();
            if (StringUtils.isNotBlank(flinkDriverUrl)) {
                String uri = flinkDriverUrl + "/flinkDriver/getDriverResource";
                try {
                    Result<Map<String, Long>> result = restTemplate.exchange(uri, HttpMethod.GET, null,
                            new ParameterizedTypeReference<Result<Map<String, Long>>>() {
                            }).getBody();

                    assert result != null;
                    if (result.isSuccess()) {
                        long cores = result.getData().get("cores");
                        long memorys = result.getData().get("memorys");

                        if (driver.getServerCores() != cores || driver.getServerMemory() != memorys) {
                            driver.setServerCores(cores);
                            driver.setServerMemory(memorys);
                            driverService.updateEntity(driver);
                        }
                    }
                } catch (Throwable e) {
                    LOG.error("update driver {} resource failure: {}", driver.getId(), e.getMessage());
                }
            }
        });
    }
}
