package io.github.melin.flink.jobserver.core.exception;

/**
 * Job server 共享实例，操作最大运行次数
 * @author melin 2021/7/20 12:10 下午
 */
public class HttpClientException extends FlinkJobException {

    public HttpClientException(String message) {
        super(message);
    }
}
