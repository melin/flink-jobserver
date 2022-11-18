package io.github.melin.flink.jobserver.logs;

import io.github.melin.flink.jobserver.core.enums.InstanceType;
import io.github.melin.flink.jobserver.core.enums.JobType;
import io.github.melin.flink.jobserver.core.enums.RuntimeMode;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * huaixin 2022/4/14 10:09
 */
@Data
@Builder(builderClassName = "Builder", setterPrefix = "set")
public class LogTaskDto {

    private String clusterCode;

    // yarn applicationId
    private String applicationId;

    private String instanceCode;

    private JobType jobType;

    private RuntimeMode runtimeMode;

    private InstanceType instanceType;

    private String owner;

    private boolean shareDriver; //driver 是否共享

    private String flinkDriverUrl;

    private Instant scheduleTime;
}
