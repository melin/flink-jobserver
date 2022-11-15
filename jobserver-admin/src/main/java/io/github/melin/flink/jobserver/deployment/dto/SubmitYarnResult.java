package io.github.melin.flink.jobserver.deployment.dto;

import lombok.Data;

/**
 * Created by libinsong on 2020/7/22 1:04 下午
 */
@Data
public class SubmitYarnResult {

    private String applicationId;

    private String flinkDriverUrl;

    private String yarnQueue;

    public SubmitYarnResult(String applicationId, String flinkDriverUrl, String yarnQueue) {
        this.applicationId = applicationId;
        this.flinkDriverUrl = flinkDriverUrl;
        this.yarnQueue = yarnQueue;
    }
}
