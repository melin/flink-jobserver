package io.github.melin.flink.jobserver.core.service;

import io.github.melin.flink.jobserver.core.dao.ApplicationDriverDao;
import io.github.melin.flink.jobserver.core.entity.ApplicationDriver;
import io.github.melin.flink.jobserver.core.enums.DriverStatus;
import com.gitee.melin.bee.core.hibernate5.HibernateBaseDao;
import com.gitee.melin.bee.core.service.BaseServiceImpl;
import com.gitee.melin.bee.util.NetUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class ApplicationDriverService extends BaseServiceImpl<ApplicationDriver, Long> {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDriverService.class);

    @Autowired
    private ApplicationDriverDao applicationDriverDao;

    private String hostName = "";

    public ApplicationDriverService() {
        hostName = NetUtils.getLocalHost();
    }

    @Override
    public HibernateBaseDao<ApplicationDriver, Long> getHibernateBaseDao() {
        return applicationDriverDao;
    }

    @Transactional(readOnly = true)
    public ApplicationDriver queryDriverByAppId(String applicationId) {
        return this.queryByNamedParam("applicationId", applicationId);
    }

    @Transactional(readOnly = true)
    public long queryDriverCount(String clusterCode) {
        return this.queryCount("clusterCode", clusterCode);
    }

    @Transactional(readOnly = true)
    public String queryDriverAddressByAppId(String applicationId) {
        ApplicationDriver driver = this.queryDriverByAppId(applicationId);
        if (driver != null) {
            return driver.getFlinkDriverUrl();
        } else {
            return null;
        }
    }

    @Transactional
    public void updateServerRunning(ApplicationDriver driver) {
        driver.setStatus(DriverStatus.RUNNING);
        driver.setGmtModified(Instant.now());
        driver.setInstanceCount(driver.getInstanceCount() + 1);
        this.updateEntity(driver);
    }

    @Transactional
    public void updateServerFinished(String appId) {
        try {
            String hql = "update FlinkDriver set status=:afterStatus, gmtModified=:gmtModified " +
                    "where status=:beforeStatus and applicationId=:appId";

            int batch = this.deleteOrUpdateByHQL(hql,
                    new String[]{"afterStatus", "gmtModified", "beforeStatus", "appId"},
                    new Object[]{DriverStatus.FINISHED, Instant.now(), DriverStatus.RUNNING, appId});
            if (batch == 1) {
                LOG.info("update driver: {} status finished", appId);
            }
        } catch (Exception e) {
            LOG.error("update driver locked error: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ApplicationDriver> queryAllIdleDrivers(String clusterCode) {
        return findByNamedParamAndOrder(new String[] {"status", "clusterCode"},
                new Object[]{DriverStatus.IDLE, clusterCode},
                Order.asc("gmtModified"));
    }

    @Transactional
    public void updateYarnQueue(String appId, String yarnQueue) {
        ApplicationDriver driver = queryDriverByAppId(appId);
        if (driver != null) {
            driver.setYarnQueue(yarnQueue);
            this.updateEntity(driver);
        }
    }

    @Transactional
    public void updateDriverStatusIdle(String appId) {
        ApplicationDriver driver = queryDriverByAppId(appId);
        if (driver != null) {
            driver.setStatus(DriverStatus.IDLE);
            driver.setGmtModified(Instant.now());
            this.updateEntity(driver);
            LOG.info("update driver: {} status finished", appId);
        }
    }

    @Transactional
    public void deleteJobServerByAppId(String appId) {
        ApplicationDriver driver = queryDriverByAppId(appId);
        if (driver != null) {
            LOG.info("delete driver: {}, appId: {}", driver.getId(), driver.getApplicationId());
            this.deleteEntity(driver);
        }
    }

    /**
     * 清空driver logThread hostName
     */
    @Transactional
    public void clearCurrentLogServer() {
        try {
            String hql = "update ApplicationDriver set logServer = null where logServer = :logServer";
            int count = this.deleteOrUpdateByHQL(hql, "logServer", hostName);
            LOG.info("清空log server count: {}", count);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Transactional
    public boolean lockCurrentLogServer(String appId) {
        try {
            String hql = "update ApplicationDriver set logServer=:logServer where logServer is null and applicationId=:appId";
            int count = this.deleteOrUpdateByHQL(hql, new String[]{"logServer", "appId"},
                    new Object[]{hostName, appId});
            if (count > 0) {
                LOG.info("获取 log server applicationId: {}", appId);
                return true;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return false;
    }

    @Transactional
    public List<ApplicationDriver> queryEmptyLogServers() {
        Criterion logServerCtr = Restrictions.isNull("logServer");
        return findByCriterions(logServerCtr);
    }

    @Transactional
    public int updateServerLocked(String applicationId, int version) {
        String hql = "update ApplicationDriver set status=:afterStatus, gmtModified=:gmtModified, version=:afterVersion " +
                "where status=:beforeStatus and applicationId=:applicationId and version =:beforeVersion";

        int batch = this.deleteOrUpdateByHQL(hql, new String[]{"afterStatus", "gmtModified", "afterVersion",
                        "beforeStatus", "applicationId", "beforeVersion"},
                new Object[]{DriverStatus.LOCKED, Instant.now(), version + 1, DriverStatus.IDLE, applicationId, version});
        if (batch == 1) {
            LOG.info("update driver: {} status locked", applicationId);
        }
        return batch;
    }

    @Transactional(readOnly = true)
    public List<ApplicationDriver> queryAvailableApplication(int maxInstanceCount, Long minJobserverId) {
        Criterion statusCrt = Restrictions.eq("status", DriverStatus.IDLE);
        Criterion shareDriverCrt = Restrictions.eq("shareDriver", true);
        Criterion instanceCountCrt = Restrictions.lt("instanceCount", maxInstanceCount);

        if (minJobserverId > 0) {
            Criterion idCrt = Restrictions.gt("id", minJobserverId);
            return findByCriterions(Order.asc("gmtCreated"), statusCrt, shareDriverCrt,
                    instanceCountCrt, idCrt);
        } else {
            return findByCriterions(Order.asc("gmtCreated"), statusCrt, shareDriverCrt, instanceCountCrt);
        }
    }
}
