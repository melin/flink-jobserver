package io.github.melin.flink.jobserver.api;

public class FlinkJobServerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FlinkJobServerException(String msg) {
		super(msg);
	}

	public FlinkJobServerException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
