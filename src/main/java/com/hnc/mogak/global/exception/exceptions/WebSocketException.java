package com.hnc.mogak.global.exception.exceptions;

import com.hnc.mogak.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class WebSocketException extends RuntimeException{

    private final ErrorCode errorCode;

    public WebSocketException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
