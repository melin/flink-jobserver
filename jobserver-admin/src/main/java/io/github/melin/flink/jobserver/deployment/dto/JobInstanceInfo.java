package io.github.melin.flink.jobserver.deployment.dto;

import io.github.melin.flink.jobserver.core.enums.InstanceType;
import io.github.melin.flink.jobserver.core.enums.JobType;
import io.github.melin.flink.jobserver.core.enums.RuntimeMode;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.time.Instant;
import java.util.List;

/**
 * huaixin 2022/4/3 2:46 PM
 */
@Data
@Builder(builderClassName = "Builder", setterPrefix = "set")
public class JobInstanceInfo {

    private String accessKey;

    private String clusterCode;

    private JobType jobType;

    private InstanceType instanceType;

    private RuntimeMode runtimeMode;

    private String instanceCode;

    private String jobName;

    private String jobText;

    private String jobConfig;

    private String yarnQueue;

    private Instant scheduleTime;

    private String owner;

    /**
     * 用户作业设置有效参数
     */
    private List<JobParam> customParams;

    @Tolerate
    public JobInstanceInfo() {
    }

    public DriverDeploymentInfo buildDriverDeploymentInfo() {
        return DriverDeploymentInfo.builder()
                .setClusterCode(this.clusterCode)
                .setRuntimeMode(this.runtimeMode)
                .setJobConfig(this.jobConfig)
                .setYarnQueue(this.yarnQueue)
                .build();
    }
}
