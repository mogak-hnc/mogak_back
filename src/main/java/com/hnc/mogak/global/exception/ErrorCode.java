package com.hnc.mogak.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth Exception
    AUTHENTICATION_FAILED("인증에 실패하셨습니다.", HttpStatus.UNAUTHORIZED),

    // Member Exception
    NOT_EXISTS_MEMBER("해당 회원이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String description;
    private final HttpStatus httpStatus;
}
