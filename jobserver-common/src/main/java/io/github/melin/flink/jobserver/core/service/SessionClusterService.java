package io.github.melin.flink.jobserver.core.service;

import com.gitee.melin.bee.core.hibernate5.HibernateBaseDao;
import com.gitee.melin.bee.core.service.BaseServiceImpl;
import io.github.melin.flink.jobserver.core.dao.SessionClusterDao;
import io.github.melin.flink.jobserver.core.entity.SessionCluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SessionClusterService extends BaseServiceImpl<SessionCluster, Long> {

    private static final Logger LOG = LoggerFactory.getLogger(SessionClusterService.class);

    @Autowired
    private SessionClusterDao sessionClusterDao;

    @Override
    public HibernateBaseDao<SessionCluster, Long> getHibernateBaseDao() {
        return this.sessionClusterDao;
    }

    @Transactional(readOnly = true)
    public SessionCluster queryDriverByAppId(String applicationId) {
        return this.queryByNamedParam("applicationId", applicationId);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public long queryDriverCount(String clusterCode) {
        return this.queryCount("clusterCode", clusterCode);
    }
}
