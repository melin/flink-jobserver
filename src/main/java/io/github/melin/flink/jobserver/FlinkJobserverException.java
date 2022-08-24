package io.github.melin.flink.jobserver;

public class FlinkJobserverException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FlinkJobserverException(String msg) {
		super(msg);
	}

	public FlinkJobserverException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
