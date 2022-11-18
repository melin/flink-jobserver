package io.github.melin.flink.jobserver.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gitee.melin.bee.core.enums.BaseStringEnum;
import com.gitee.melin.bee.core.enums.jackson.JacksonEnumStringSerializer;

@JsonSerialize(using = JacksonEnumStringSerializer.class)
public enum DeploymentMode implements BaseStringEnum {
    APPLICATION,
    SESSION;

    @JsonValue
    @Override
    public String getValue() {
        return this.name();
    }
}
