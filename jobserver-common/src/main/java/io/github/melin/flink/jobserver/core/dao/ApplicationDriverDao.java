package io.github.melin.flink.jobserver.core.dao;

import io.github.melin.flink.jobserver.core.entity.ApplicationDriver;
import com.gitee.melin.bee.core.hibernate5.HibernateBaseDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * huaixin 2022/3/28 11:57 AM
 */
@Repository
public class ApplicationDriverDao extends HibernateBaseDaoImpl<ApplicationDriver, Long> {
}
