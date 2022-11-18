package io.github.melin.flink.jobserver.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gitee.melin.bee.core.enums.BaseStringEnum;
import com.gitee.melin.bee.core.enums.jackson.JacksonEnumStringSerializer;

@JsonSerialize(using = JacksonEnumStringSerializer.class)
public enum RuntimeMode implements BaseStringEnum {
    BATCH,
    STREAMING;

    @JsonValue
    @Override
    public String getValue() {
        return this.name();
    }

    public static RuntimeMode fromString(String code) {
        for (RuntimeMode type : RuntimeMode.values()) {
            if (type.getValue().equalsIgnoreCase(code)) {
                return type;
            }
        }

        return null;
    }
}
