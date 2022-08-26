package io.github.melin.flink.jobserver.core.exception;

public class SparkJobException extends RuntimeException {
    public SparkJobException(String message){
        super(message);
    }

    public SparkJobException(String message, Throwable cause) {
        super(message, cause);
    }
}
