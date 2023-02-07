package io.github.melin.flink.jobserver.api;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.helpers.MessageFormatter;

/**
 * @author melin 2021/8/18 10:53 上午
 */
public class LogUtils {

    public static void info(String format, Object... params) {
        String message = format;
        if (params.length > 0) {
            message = MessageFormatter.arrayFormat(format, params).getMessage();
        }

        try {
            Class<?> clazz = ClassUtils.getClass("io.github.melin.flink.jobserver.driver.util.LogUtils");
            MethodUtils.invokeStaticMethod(clazz, "info", message);
        } catch (Exception ignored) {
        }
    }

    public static void warn(String format, Object... params) {
        String message = format;
        if (params.length > 0) {
            message = MessageFormatter.arrayFormat(format, params).getMessage();
        }

        try {
            Class<?> clazz = ClassUtils.getClass("io.github.melin.flink.jobserver.driver.util.LogUtils");
            MethodUtils.invokeStaticMethod(clazz, "warn", message);
        } catch (Exception ignored) {
        }
    }

    public static void error(String format, Object... params) {
        String message = format;
        if (params.length > 0) {
            message = MessageFormatter.arrayFormat(format, params).getMessage();
        }

        try {
            Class<?> clazz = ClassUtils.getClass("io.github.melin.flink.jobserver.driver.util.LogUtils");
            MethodUtils.invokeStaticMethod(clazz, "error", message);
        } catch (Exception ignored) {
        }
    }

    public static void stdout(String format, Object... params) {
        String message = format;
        if (params.length > 0) {
            message = MessageFormatter.arrayFormat(format, params).getMessage();
        }

        try {
            Class<?> clazz = ClassUtils.getClass("io.github.melin.flink.jobserver.driver.util.LogUtils");
            MethodUtils.invokeStaticMethod(clazz, "stdout", message);
        } catch (Exception ignored) {
        }
    }
}
