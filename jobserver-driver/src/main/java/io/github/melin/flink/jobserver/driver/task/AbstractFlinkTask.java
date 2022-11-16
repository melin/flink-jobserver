package io.github.melin.flink.jobserver.driver.task;

import com.gitee.melin.bee.core.support.Result;
import io.github.melin.flink.jobserver.core.dto.InstanceDto;

public abstract class AbstractFlinkTask {
    public Result<String> runTask(InstanceDto instanceDto) {
        return Result.successMessageResult("");
    }

    public void killJob(String instanceCode) {
    }
}
