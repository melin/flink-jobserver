package io.github.melin.flink.jobserver.web.controller;

import com.gitee.melin.bee.core.support.Pagination;
import com.gitee.melin.bee.core.support.Result;
import com.gitee.melin.bee.util.NetUtils;
import com.google.common.collect.Lists;
import io.github.melin.flink.jobserver.FlinkJobServerConf;
import io.github.melin.flink.jobserver.core.entity.ApplicationDriver;
import io.github.melin.flink.jobserver.core.entity.Cluster;
import io.github.melin.flink.jobserver.core.enums.DeployMode;
import io.github.melin.flink.jobserver.core.enums.DriverStatus;
import io.github.melin.flink.jobserver.core.service.ApplicationDriverService;
import io.github.melin.flink.jobserver.core.service.ClusterService;
import io.github.melin.flink.jobserver.submit.deployer.YarnSessionClusterDeployer;
import io.github.melin.flink.jobserver.support.ClusterConfig;
import io.github.melin.flink.jobserver.support.YarnClientService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;

@Controller
public class SessionClusterController {

    private static final Logger LOG = LoggerFactory.getLogger(SessionClusterController.class);

    private static final String hostName = NetUtils.getLocalHost();

    @Autowired
    private ApplicationDriverService driverService;

    @Autowired
    private YarnSessionClusterDeployer yarnSessionClusterDeployer;

    @Autowired
    private ClusterConfig clusterConfig;

    @Autowired
    private YarnClientService yarnClientService;

    @Autowired
    private ClusterService clusterService;

    public static String flinkLauncherFailedMsg = "";

    @RequestMapping("/session")
    public String home(ModelMap model) {
        return "session";
    }

    @RequestMapping("/session/queryClusters")
    @ResponseBody
    public Pagination<ApplicationDriver> queryClusters(String applicationId, int page, int limit, HttpServletRequest request) {
        String sort = request.getParameter("sort");
        String order = request.getParameter("order");

        Order order1 = Order.desc("gmtModified");
        if (StringUtils.isNotEmpty(sort)) {
            if ("asc".equals(order)) {
                order1 = Order.asc(sort);
            } else {
                order1 = Order.desc(sort);
            }
        }

        List<String> params = Lists.newArrayList();
        List<Object> values = Lists.newArrayList();
        if (StringUtils.isNotBlank(applicationId)) {
            params.add("applicationId");
            values.add(applicationId);
        }

        params.add("deployMode");
        values.add(DeployMode.SESSION);

        Pagination<ApplicationDriver> pagination = driverService.findPageByNamedParamAndOrder(params, values,
                Lists.newArrayList(order1), page, limit);

        pagination.getResult().forEach(driver -> {
            String url = clusterConfig.getValue(driver.getClusterCode(), FlinkJobServerConf.JOBSERVER_YARN_PROXY_URI);
            driver.setFlinkYarnProxyUri(url);
        });

        return pagination;
    }

    @RequestMapping("/session/queryCluster")
    @ResponseBody
    public Result<ApplicationDriver> queryCluster(Long clusterId) {
        try {
            ApplicationDriver cluster = driverService.getEntity(clusterId);
            return Result.successDataResult(cluster);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Result.failureResult(e.getMessage());
        }
    }

    @RequestMapping("/session/saveCluster")
    @ResponseBody
    public Result<Void> saveCluster(ApplicationDriver sessionDriver) {
        try {
            sessionDriver.setGmtCreated(Instant.now());
            sessionDriver.setGmtModified(Instant.now());

            Cluster cluster = clusterService.getClusterByCode(sessionDriver.getClusterCode());

            if (sessionDriver.getId() == null) {
                sessionDriver.setSchedulerType(cluster.getSchedulerType());
                sessionDriver.setLogServer(hostName);
                sessionDriver.setDeployMode(DeployMode.SESSION);
                sessionDriver.setStatus(DriverStatus.CLOSED);
                sessionDriver.setCreater("jobserver");
                sessionDriver.setModifier("jobserver");
                driverService.insertEntity(sessionDriver);
            } else {
                ApplicationDriver old = driverService.getEntity(sessionDriver.getId());
                old.setConfig(sessionDriver.getConfig());
                old.setClusterCode(sessionDriver.getClusterCode());
                old.setGmtModified(Instant.now());
                driverService.updateEntity(old);
            }
            return Result.successResult();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Result.failureResult(e.getMessage());
        }
    }

    @RequestMapping("/session/deleteCluster")
    @ResponseBody
    public Result<Void> deleteCluster(Long clusterId) {
        try {
            ApplicationDriver cluster = driverService.getEntity(clusterId);
            driverService.deleteEntity(cluster);
            return Result.successResult();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Result.failureResult(e.getMessage());
        }
    }

    @RequestMapping("/session/startCluster")
    @ResponseBody
    public Result<Void> startCluster(Long clusterId) {
        try {
            ApplicationDriver sessionCluster = driverService.getEntity(clusterId);
            if (sessionCluster != null) {
                yarnSessionClusterDeployer.startSessionCluster(sessionCluster);
                return Result.successResult();
            }
            return Result.failureResult("Session cluster not exists");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Result.failureResult(e.getMessage());
        }
    }

    @RequestMapping("/session/closeCluster")
    @ResponseBody
    public Result<Void> closeCluster(Long clusterId) {
        try {
            ApplicationDriver cluster = driverService.getEntity(clusterId);
            if (cluster != null) {
                yarnClientService.killYarnApp(cluster.getClusterCode(), cluster.getApplicationId());

                cluster.setStatus(DriverStatus.CLOSED);
                cluster.setApplicationId(null);
                driverService.updateEntity(cluster);
                return Result.successResult();
            }

            return Result.failureResult("Session cluster not exists");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Result.failureResult(e.getMessage());
        }
    }
}
