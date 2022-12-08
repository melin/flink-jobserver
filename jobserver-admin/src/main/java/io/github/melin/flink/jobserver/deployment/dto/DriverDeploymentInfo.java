package io.github.melin.flink.jobserver.deployment.dto;

import io.github.melin.flink.jobserver.core.enums.RuntimeMode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder(builderClassName = "Builder", setterPrefix = "set")
public class DriverDeploymentInfo {

    private RuntimeMode runtimeMode;

    private String jobConfig;

    private String clusterCode;

    private String yarnQueue;
}
