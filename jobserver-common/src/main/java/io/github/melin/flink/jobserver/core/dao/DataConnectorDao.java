package io.github.melin.flink.jobserver.core.dao;

import com.gitee.melin.bee.core.hibernate5.HibernateBaseDaoImpl;
import io.github.melin.flink.jobserver.core.entity.DataConnector;
import org.springframework.stereotype.Repository;

@Repository
public class DataConnectorDao extends HibernateBaseDaoImpl<DataConnector, Long> {

}
