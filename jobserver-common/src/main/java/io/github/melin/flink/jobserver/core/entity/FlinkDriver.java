package io.github.melin.flink.jobserver.core.entity;

import io.github.melin.flink.jobserver.core.enums.DriverResType;
import io.github.melin.flink.jobserver.core.enums.DriverStatus;
import com.gitee.melin.bee.model.IEntity;
import com.gitee.melin.bee.util.NetUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;

import static io.github.melin.flink.jobserver.core.enums.DriverResType.YARN_BATCH;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "fjs_flink_driver")
public class FlinkDriver implements IEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "cluster_code")
    private String clusterCode;

    @Column(name = "version")
    private Integer version;

    @Column(name = "server_ip", length = 100)
    private String serverIp;

    @Column(name = "server_port", nullable = false)
    private Integer serverPort;

    @Column(name = "driver_res_type")
    @Type(type = "com.gitee.melin.bee.core.enums.StringValuedEnumType",
            parameters = {@org.hibernate.annotations.Parameter(name = "enumClass",
                    value = "io.github.melin.flink.jobserver.core.enums.DriverResType")})
    private DriverResType driverResType;

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
    private Long serverCores;

    @Column(name = "server_memory", nullable = false)
    private Long serverMemory;

    @Column(name = "share_driver")
    private boolean shareDriver = true;

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

    public static FlinkDriver buildFlinkDriver(String clusterCode, Boolean shareDriver) {
        FlinkDriver jobServer = new FlinkDriver();
        jobServer.setClusterCode(clusterCode);
        jobServer.setVersion(0);
        jobServer.setServerIp("0.0.0.0");
        jobServer.setServerPort(-1);
        jobServer.setDriverResType(YARN_BATCH);
        jobServer.setStatus(DriverStatus.INIT);
        jobServer.setApplicationId("");
        jobServer.setCreater("");
        jobServer.setGmtCreated(Instant.now());
        jobServer.setGmtModified(Instant.now());
        jobServer.setInstanceCount(0);
        jobServer.setServerCores(0L);
        jobServer.setServerMemory(0L);
        jobServer.setShareDriver(shareDriver);
        jobServer.setLogServer(hostName);
        return jobServer;
    }
}
