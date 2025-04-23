package com.hnc.mogak.global.exception.exceptions;

import com.hnc.mogak.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class MogakZoneException extends RuntimeException{

    private final ErrorCode errorCode;

    public MogakZoneException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }
}
