package com.hnc.mogak.global.exception.exceptions;

import com.hnc.mogak.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class BadgeException extends RuntimeException{

    private final ErrorCode errorCode;

    public BadgeException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

}
