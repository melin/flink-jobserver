package io.github.melin.flink.jobserver.submit.dto;

import io.github.melin.flink.jobserver.core.enums.DriverInstance;
import lombok.Data;

@Data
public class JobSubmitResult {

    private DriverInstance status;

    private String message;

    private Long jobserverId;

    public JobSubmitResult() {
    }

    public JobSubmitResult(DriverInstance status, String message) {
        this.status = status;
        this.message = message;
    }

    public JobSubmitResult(DriverInstance status, Long jobserverId) {
        this.status = status;
        this.jobserverId = jobserverId;
    }
}
