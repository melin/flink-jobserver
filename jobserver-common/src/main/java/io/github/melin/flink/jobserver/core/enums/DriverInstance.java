package io.github.melin.flink.jobserver.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by admin on 2019/10/29 11:49 上午
 */
public enum DriverInstance {
    SHARE_INSTANCE(0),

    NEW_INSTANCE(1),

    ERROR_INSTANCE(2);

    private Integer value;

    DriverInstance(Integer value) {
        this.value = value;
    }

    @JsonValue
    public Integer getValue() {
        return value;
    }
}