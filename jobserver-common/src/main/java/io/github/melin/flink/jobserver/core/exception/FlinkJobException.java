package io.github.melin.flink.jobserver.core.exception;

public class FlinkJobException extends RuntimeException {
    public FlinkJobException(String message){
        super(message);
    }

    public FlinkJobException(String message, Throwable cause) {
        super(message, cause);
    }
}
