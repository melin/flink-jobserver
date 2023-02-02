package io.github.melin.flink.jobserver.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gitee.melin.bee.core.enums.BaseStringEnum;
import com.gitee.melin.bee.core.enums.jackson.JacksonEnumStringSerializer;

/**
 * huaixin 2022/4/6 10:44 PM
 */
@JsonSerialize(using = JacksonEnumStringSerializer.class)
public enum SessionClusterStatus implements BaseStringEnum {

    CLOSED("关闭", "closed"),

    INIT("初始化", "init"),

    RUNNING("运行", "running");

    private final String name;

    private final String value;

    SessionClusterStatus(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    @JsonValue
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
