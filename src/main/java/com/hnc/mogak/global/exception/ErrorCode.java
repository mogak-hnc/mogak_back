package com.hnc.mogak.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth Exception
    AUTHENTICATION_FAILED("인증에 실패하셨습니다.", HttpStatus.UNAUTHORIZED),

    // MogakZone Exception
    NOT_EXISTS_MOGAKZONE("모각존이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_EXISTS_HOST_MEMBER("방장이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    FULL_CAPACITY("모각존 정원이 가득 찼습니다.", HttpStatus.BAD_REQUEST),
    INVALID_ZONE_PASSWORD("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    LOGIN_REQUIRED_FOR_JOIN("로그인이 필요합니다.", HttpStatus.BAD_REQUEST),
    ALREADY_JOINED("이미 가입되었습니다.", HttpStatus.BAD_REQUEST),

    // CHALLENGE Exception
    NOT_EXISTS_CHALLENGE("챌린지가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),

    // CHALLENGE_ARTICLE Exception
    UPLOAD_FAILED("파일 업로드에 실패하였습니다.", HttpStatus.BAD_REQUEST),

    // Member Exception
    NOT_EXISTS_MEMBER("해당 회원이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
    


    private final String description;
    private final HttpStatus httpStatus;
}
