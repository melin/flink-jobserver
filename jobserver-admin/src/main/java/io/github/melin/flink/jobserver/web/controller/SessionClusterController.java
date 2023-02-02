package io.github.melin.flink.jobserver.web.controller;

import com.gitee.melin.bee.core.support.Pagination;
import com.gitee.melin.bee.core.support.Result;
import com.google.common.collect.Lists;
import io.github.melin.flink.jobserver.FlinkJobServerConf;
import io.github.melin.flink.jobserver.core.entity.SessionCluster;
import io.github.melin.flink.jobserver.core.service.SessionClusterService;
import io.github.melin.flink.jobserver.support.ClusterConfig;
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

    @Autowired
    private SessionClusterService driverService;

    @Autowired
    private SessionClusterService sessionClusterService;

    @Autowired
    private ClusterConfig clusterConfig;

    @RequestMapping("/session")
    public String home(ModelMap model) {
        return "session";
    }

    @RequestMapping("/session/queryClusters")
    @ResponseBody
    public Pagination<SessionCluster> queryClusters(String applicationId, int page, int limit, HttpServletRequest request) {
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
        Pagination<SessionCluster> pagination = driverService.findPageByNamedParamAndOrder(params, values,
                Lists.newArrayList(order1), page, limit);

        pagination.getResult().forEach(driver -> {
            String url = clusterConfig.getValue(driver.getClusterCode(), FlinkJobServerConf.JOBSERVER_YARN_PROXY_URI);
            driver.setFlinkYarnProxyUri(url);
        });

        return pagination;
    }

    @RequestMapping("/session/queryCluster")
    @ResponseBody
    public Result<SessionCluster> queryCluster(Long clusterId) {
        try {
            SessionCluster cluster = sessionClusterService.getEntity(clusterId);
            return Result.successDataResult(cluster);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Result.failureResult(e.getMessage());
        }
    }

    @RequestMapping("/session/saveCluster")
    @ResponseBody
    public Result<Void> saveCluster(SessionCluster cluster) {
        try {
            cluster.setGmtCreated(Instant.now());
            cluster.setGmtModified(Instant.now());

            if (cluster.getId() == null) {
                cluster.setCreater("jobserver");
                cluster.setModifier("jobserver");
                sessionClusterService.insertEntity(cluster);
            } else {
                SessionCluster old = sessionClusterService.getEntity(cluster.getId());
                old.setConfig(cluster.getConfig());
                old.setClusterCode(cluster.getClusterCode());
                old.setGmtModified(Instant.now());
                sessionClusterService.updateEntity(old);
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
            SessionCluster cluster = sessionClusterService.getEntity(clusterId);
            sessionClusterService.deleteEntity(cluster);
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
            return Result.successResult();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Result.failureResult(e.getMessage());
        }
    }

    @RequestMapping("/session/closeCluster")
    @ResponseBody
    public Result<Void> closeCluster(Long clusterId) {
        try {
            return Result.successResult();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Result.failureResult(e.getMessage());
        }
    }
}
