package io.github.melin.flink.jobserver.driver.model;

import com.beust.jcommander.IStringConverter;
import org.apache.flink.api.common.RuntimeExecutionMode;

/**
 * huaixin 2022/4/14 19:13
 */
public class RuntimeModeConverter implements IStringConverter<RuntimeExecutionMode> {

    @Override
    public RuntimeExecutionMode convert(String value) {
        RuntimeExecutionMode convertedValue = RuntimeExecutionMode.valueOf(value);
        return convertedValue;
    }
}
