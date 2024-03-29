package io.github.melin.flink.jobserver.support;

import io.github.melin.flink.jobserver.core.entity.ApplicationDriver;
import io.github.melin.flink.jobserver.core.service.ApplicationDriverService;
import io.github.melin.flink.jobserver.core.util.LogRecord;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * huaixin 2022/4/14 20:35
 */
@Service
public class DriverClientService {

    private static final Logger LOG = LoggerFactory.getLogger(DriverClientService.class);

    @Autowired
    private ApplicationDriverService driverService;

    @Autowired
    private YarnClientService yarnClientService;

    @Autowired
    private RestTemplate restTemplate;

    public List<LogRecord> getServerLog(String flinkDriverUrl, String instanceCode) {
        String uri = flinkDriverUrl + "/flinkDriver/getServerLog?instanceCode=" + instanceCode;
        try {
            ResponseEntity<List<LogRecord>> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<LogRecord>>() {});
            if (response.hasBody()) {
                return response.getBody();
            } else {
                return null;
            }
        } catch (Exception e) {
            LOG.error("get task {} log error: {}", instanceCode, e.getMessage());
            return null;
        }
    }

    public Boolean isFlinkJobRunning(String flinkDriverUrl, String instanceCode, String applicationId) {
        String url = flinkDriverUrl + "/flinkDriver/isJobRunning?instanceCode=" + instanceCode;
        try {
            return restTemplate.postForObject(url, null, Boolean.class);
        } catch (Exception e) {
            ApplicationDriver driver = driverService.queryDriverByAppId(applicationId);
            if (driver != null) {
                YarnApplicationState state = yarnClientService.getApplicationStatus(driver.getClusterCode(), applicationId);

                String newFlinkDriverUrl = driver.getFlinkDriverUrl();
                if (!newFlinkDriverUrl.equals(flinkDriverUrl)) {
                    LOG.info("driver 重启，切换到新的节点：{}: {}", driver.getServerIp(), driver.getServerPort());
                }

                return YarnApplicationState.RUNNING == state;
            }
            return false;
        }
    }
}
