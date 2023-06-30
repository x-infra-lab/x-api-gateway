package io.github.xinfra.lab.gateway.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 4xxxx mean client error
    ROUTE_NO_MATCH(40001, "Route no match"),

    // 5xxxx mean server error
    SYSTEM_EXCEPTION(50001, "System exception"),
    ;
    int code;
    String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


}
