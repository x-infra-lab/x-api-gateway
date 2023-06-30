package io.github.xinfra.lab.gateway.exception;

import io.github.xinfra.lab.gateway.commons.Assert;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;

@Data
public class ErrorResponseException extends Throwable {
    private HttpResponseStatus status;
    private HttpHeaders headers;
    private ErrorCode errorCode;


    public ErrorResponseException(HttpResponseStatus status, ErrorCode errorCode) {
        this(status, null, errorCode);
    }

    public ErrorResponseException(HttpResponseStatus status, HttpHeaders headers, ErrorCode errorCode) {
        Assert.notNull(status, "status must not null.");
        Assert.notNull(errorCode, "errorCode must not null.");
        this.status = status;
        this.headers = headers;
        this.errorCode = errorCode;
    }


}
