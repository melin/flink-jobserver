package io.github.melin.flink.jobserver.core.entity;

import com.gitee.melin.bee.model.IEntity;
import com.gitee.melin.bee.util.NetUtils;
import io.github.melin.flink.jobserver.core.enums.SessionClusterStatus;
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
@Table(name = "fjs_session_cluster")
public class SessionCluster implements IEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "session_name")
    private String sessionName;

    @Column(name = "cluster_code")
    private String clusterCode;

    @Column(name = "config")
    private String config;

    @Column(name = "version")
    private Integer version;

    @Column(name = "server_ip", length = 100)
    private String serverIp;

    @Column(name = "server_port", nullable = false)
    private Integer serverPort;

    @Type(type = "com.gitee.melin.bee.core.enums.StringValuedEnumType",
            parameters = {@org.hibernate.annotations.Parameter(name = "enumClass",
                    value = "io.github.melin.flink.jobserver.core.enums.SessionClusterStatus")})
    @Column(name = "status", nullable = false, length = 45)
    private SessionClusterStatus status = SessionClusterStatus.CLOSED;

    @Column(name = "application_id", nullable = false, length = 64)
    private String applicationId;

    @Column(name = "log_server", length = 64)
    private String logServer;

    @Column(name = "server_cores", nullable = false)
    private Integer serverCores;

    @Column(name = "server_memory", nullable = false)
    private Long serverMemory;

    @Column(name = "creater", length = 45)
    private String creater;

    @Column(name = "modifier", length = 45)
    private String modifier;

    @Column(name = "gmt_created", nullable = false)
    private Instant gmtCreated;

    @Column(name = "gmt_modified")
    private Instant gmtModified;

    @Transient
    private String flinkYarnProxyUri;

    public String getFlinkDriverUrl() {
        if ("0.0.0.0".equals(serverIp)) {
            return null;
        }
        return "http://" + serverIp + ":" + serverPort;
    }

    private static final String hostName = NetUtils.getLocalHost();

    public static SessionCluster buildSessionDriver(String clusterCode, String sessionName) {
        SessionCluster jobServer = new SessionCluster();
        jobServer.setSessionName(sessionName);
        jobServer.setClusterCode(clusterCode);
        jobServer.setVersion(0);
        jobServer.setServerIp("0.0.0.0");
        jobServer.setServerPort(-1);
        jobServer.setStatus(SessionClusterStatus.INIT);
        jobServer.setApplicationId("");
        jobServer.setCreater("");
        jobServer.setGmtCreated(Instant.now());
        jobServer.setGmtModified(Instant.now());
        jobServer.setServerCores(0);
        jobServer.setServerMemory(0L);
        jobServer.setLogServer(hostName);
        return jobServer;
    }
}
