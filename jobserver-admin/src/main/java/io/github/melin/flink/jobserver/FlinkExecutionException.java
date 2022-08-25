package io.github.melin.flink.jobserver;

public class FlinkExecutionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FlinkExecutionException(String message) {
		super(message);
	}

	public FlinkExecutionException(String message, Throwable e) {
		super(message, e);
	}
}
