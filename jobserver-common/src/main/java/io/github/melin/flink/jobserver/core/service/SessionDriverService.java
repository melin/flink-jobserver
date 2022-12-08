package io.github.melin.flink.jobserver.core.service;

import com.gitee.melin.bee.core.hibernate5.HibernateBaseDao;
import com.gitee.melin.bee.core.service.BaseServiceImpl;
import io.github.melin.flink.jobserver.core.dao.SessionDriverDao;
import io.github.melin.flink.jobserver.core.entity.SessionDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SessionDriverService extends BaseServiceImpl<SessionDriver, Long> {

    private static final Logger LOG = LoggerFactory.getLogger(SessionDriverService.class);

    @Autowired
    private SessionDriverDao sessionDriverDao;

    @Override
    public HibernateBaseDao<SessionDriver, Long> getHibernateBaseDao() {
        return this.sessionDriverDao;
    }

    @Transactional(readOnly = true)
    public SessionDriver queryDriverByAppId(String applicationId) {
        return this.queryByNamedParam("applicationId", applicationId);
    }
}
