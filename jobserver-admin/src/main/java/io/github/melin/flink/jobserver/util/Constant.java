package io.github.melin.flink.jobserver.util;

/**
 * huaixin 2022/3/30 2:22 PM
 */
public interface Constant {

    /**
     * 如果作业实例没有上游实例，scheduleCode 默认值
     */
    String ROOT_SCHEDULE_NODE = "START";

    String YARN_APPLICATION_TYPE = "flink_jobserver";
}
