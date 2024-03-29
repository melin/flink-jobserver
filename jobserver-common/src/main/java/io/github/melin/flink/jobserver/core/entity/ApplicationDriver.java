package io.github.melin.flink.jobserver.core.entity;

import io.github.melin.flink.jobserver.core.enums.DeployMode;
import io.github.melin.flink.jobserver.core.enums.DriverStatus;
import com.gitee.melin.bee.model.IEntity;
import com.gitee.melin.bee.util.NetUtils;
import io.github.melin.flink.jobserver.core.enums.RuntimeMode;
import io.github.melin.flink.jobserver.core.enums.SchedulerType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "fjs_application_driver")
public class ApplicationDriver implements IEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "cluster_code")
    private String clusterCode;

    @Column(name = "deploy_mode", length = 32)
    @Type(type = "com.gitee.melin.bee.core.enums.StringValuedEnumType",
            parameters = {@org.hibernate.annotations.Parameter(name = "enumClass",
                    value = "io.github.melin.flink.jobserver.core.enums.DeployMode")})
    private DeployMode deployMode = DeployMode.APPLICATION;

    @Column(name = "session_name")
    private String sessionName;

    @Column(name = "config")
    private String config;

    @Column(name = "version")
    private Integer version;

    @Column(name = "server_ip", length = 100)
    private String serverIp;

    @Column(name = "server_port", nullable = false)
    private Integer serverPort = -1;

    @Column(name = "scheduler_type")
    @Type(type = "com.gitee.melin.bee.core.enums.StringValuedEnumType",
            parameters = {@org.hibernate.annotations.Parameter(name = "enumClass",
                    value = "io.github.melin.flink.jobserver.core.enums.SchedulerType")})
    private SchedulerType schedulerType; // 调度框架:YARN、K8S

    @Type(type = "com.gitee.melin.bee.core.enums.StringValuedEnumType",
            parameters = {@org.hibernate.annotations.Parameter(name = "enumClass",
                    value = "io.github.melin.flink.jobserver.core.enums.DriverStatus")})
    @Column(name = "status", nullable = false, length = 45)
    private DriverStatus status;

    @Column(name = "application_id", nullable = false, length = 64)
    private String applicationId;

    @Column(name = "log_server", length = 64)
    private String logServer;

    @Column(name = "instance_count")
    private Integer instanceCount;

    @Column(name = "server_cores", nullable = false)
    private Integer serverCores;

    @Column(name = "server_memory", nullable = false)
    private Long serverMemory;

    @Column(name = "share_driver")
    private boolean shareDriver = true;

    @Column(name = "runtime_mode", length = 32)
    @Type(type = "com.gitee.melin.bee.core.enums.StringValuedEnumType",
            parameters = {@org.hibernate.annotations.Parameter(name = "enumClass",
                    value = "io.github.melin.flink.jobserver.core.enums.RuntimeMode")})
    private RuntimeMode runtimeMode;

    @Column(name = "yarn_queue")
    private String yarnQueue;

    @Column(name = "creater", length = 45)
    private String creater;

    @Column(name = "modifier", length = 45)
    private String modifier;

    @Column(name = "gmt_created", nullable = false)
    private Instant gmtCreated;

    @Column(name = "gmt_modified")
    private Instant gmtModified;

    @Formula("(select p.code from fjs_job_instance p where p.application_id = application_id and p.status='RUNNING' limit 1)")
    private String instanceCode;

    @Transient
    private String flinkYarnProxyUri;

    public String getFlinkDriverUrl() {
        if ("0.0.0.0".equals(serverIp)) {
            return null;
        }
        return "http://" + serverIp + ":" + serverPort;
    }

    private static final String hostName = NetUtils.getLocalHost();

    public static ApplicationDriver buildSessionDriver(String clusterCode, String sessionName) {
        ApplicationDriver jobServer = new ApplicationDriver();
        jobServer.setClusterCode(clusterCode);
        jobServer.setDeployMode(DeployMode.SESSION);
        jobServer.setSessionName(sessionName);
        jobServer.setVersion(0);
        jobServer.setServerIp("0.0.0.0");
        jobServer.setServerPort(-1);
        jobServer.setSchedulerType(SchedulerType.YARN);
        jobServer.setStatus(DriverStatus.INIT);
        jobServer.setApplicationId("");
        jobServer.setCreater("");
        jobServer.setGmtCreated(Instant.now());
        jobServer.setGmtModified(Instant.now());
        jobServer.setInstanceCount(0);
        jobServer.setServerCores(0);
        jobServer.setServerMemory(0L);
        jobServer.setShareDriver(false);
        jobServer.setLogServer(hostName);
        return jobServer;
    }

    public static ApplicationDriver buildApplicationDriver(String clusterCode, Boolean shareDriver) {
        ApplicationDriver jobServer = new ApplicationDriver();
        jobServer.setClusterCode(clusterCode);
        jobServer.setDeployMode(DeployMode.APPLICATION);
        jobServer.setVersion(0);
        jobServer.setServerIp("0.0.0.0");
        jobServer.setServerPort(-1);
        jobServer.setSchedulerType(SchedulerType.YARN);
        jobServer.setStatus(DriverStatus.INIT);
        jobServer.setApplicationId("");
        jobServer.setCreater("");
        jobServer.setGmtCreated(Instant.now());
        jobServer.setGmtModified(Instant.now());
        jobServer.setInstanceCount(0);
        jobServer.setServerCores(0);
        jobServer.setServerMemory(0L);
        jobServer.setShareDriver(shareDriver);
        jobServer.setLogServer(hostName);
        return jobServer;
    }
}
