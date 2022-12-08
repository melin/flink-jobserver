package io.github.melin.flink.jobserver.web.controller;

import com.gitee.melin.bee.core.support.Pagination;
import com.google.common.collect.Lists;
import io.github.melin.flink.jobserver.ConfigProperties;
import io.github.melin.flink.jobserver.FlinkJobServerConf;
import io.github.melin.flink.jobserver.core.entity.Cluster;
import io.github.melin.flink.jobserver.core.entity.SessionDriver;
import io.github.melin.flink.jobserver.core.service.ClusterService;
import io.github.melin.flink.jobserver.core.service.SessionDriverService;
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
import java.util.List;

@Controller
public class SessionDriverController {

    private static final Logger LOG = LoggerFactory.getLogger(SessionDriverController.class);

    @Autowired
    private SessionDriverService driverService;

    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ClusterConfig clusterConfig;

    @RequestMapping("/sessionDriver")
    public String home(ModelMap model) {
        List<Cluster> clusters = clusterService.queryValidClusters();
        model.addAttribute("clusters", clusters);
        return "sessionDriver";
    }

    @RequestMapping("/sessionDriver/queryDrivers")
    @ResponseBody
    public Pagination<SessionDriver> queryDrivers(String applicationId, int page, int limit, HttpServletRequest request) {
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
        Pagination<SessionDriver> pagination = driverService.findPageByNamedParamAndOrder(params, values,
                Lists.newArrayList(order1), page, limit);

        pagination.getResult().forEach(driver -> {
            String url = clusterConfig.getValue(driver.getClusterCode(), FlinkJobServerConf.JOBSERVER_YARN_PROXY_URI);
            driver.setFlinkYarnProxyUri(url);
        });

        return pagination;
    }
}
