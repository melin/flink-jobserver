package io.github.melin.flink.jobserver.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gitee.melin.bee.core.enums.BaseStringEnum;
import com.gitee.melin.bee.core.enums.jackson.JacksonEnumStringSerializer;

/**
 * Created by admin on 2017/5/21.
 */
@JsonSerialize(using = JacksonEnumStringSerializer.class)
public enum JobType implements BaseStringEnum {
    FLINK_BATCH_SQL,
    FLINK_BATCH_JAR,
    FLINK_STREAM_SQL,
    FLINK_STREAM_JAR;

    @JsonValue
    @Override
    public String getValue() {
        return this.name();
    }

    public static boolean isBatchJob(JobType jobType) {
        return FLINK_BATCH_SQL == jobType || FLINK_BATCH_JAR == jobType;
    }
}
