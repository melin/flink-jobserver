package io.github.melin.flink.jobserver.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gitee.melin.bee.core.jdbc.Connector;
import com.gitee.melin.bee.core.jdbc.DataConnectorType;
import com.gitee.melin.bee.model.IEntity;
import io.github.melin.flink.jobserver.core.util.AESUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "fjs_data_connector")
public class DataConnector implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "creater", length = 45)
    private String creater;

    @Column(name = "modifier", length = 45)
    private String modifier;

    @Column(name = "gmt_created", nullable = false)
    private Instant gmtCreated;

    @Column(name = "gmt_modified")
    private Instant gmtModified;

    private String code;

    private String name;

    @Column(name = "connector_type")
    @Type(type = "com.gitee.melin.bee.core.enums.StringValuedEnumType",
            parameters = {@org.hibernate.annotations.Parameter(name = "enumClass",
                    value = "com.gitee.melin.bee.core.jdbc.DataConnectorType")})
    private DataConnectorType connectorType;

    private String username;

    @JsonIgnore
    private String password;

    @Column(name="jdbc_url")
    private String jdbcUrl;

    public Connector buildDataConnector() {
        Connector connector = new Connector();
        connector.setCode(code);
        connector.setConnectorType(connectorType);
        connector.setUsername(username);
        String decrypt = AESUtils.decrypt(password);
        if (decrypt == null) {
            decrypt = password;
        }
        connector.setPassword(decrypt);
        connector.setJdbcUrl(jdbcUrl);
        return connector;
    }
}
