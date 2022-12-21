package io.github.melin.flink.jobserver.core.dao;

import com.gitee.melin.bee.core.hibernate5.HibernateBaseDaoImpl;
import io.github.melin.flink.jobserver.core.entity.SessionCluster;
import org.springframework.stereotype.Repository;

/**
 * huaixin 2022/3/28 11:57 AM
 */
@Repository
public class SessionClusterDao extends HibernateBaseDaoImpl<SessionCluster, Long> {
}
