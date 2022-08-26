package io.github.melin.flink.jobserver.core.exception;

/**
 * huaixin 2022/4/14 21:23
 */
public class ResouceLimitException extends SparkJobException {

    public ResouceLimitException(String message) {
        super(message);
    }
}
