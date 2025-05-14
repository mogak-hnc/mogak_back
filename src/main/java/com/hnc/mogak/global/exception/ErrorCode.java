package com.hnc.mogak.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth Exception
    AUTHENTICATION_FAILED("인증에 실패하셨습니다.", HttpStatus.UNAUTHORIZED),

    // WebSocket Exception
    EXPIRED_TOKEN("토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("유효하지 않는 토큰입니다.", HttpStatus.UNAUTHORIZED),

    // MogakZone Exception
    NOT_EXISTS_MOGAKZONE("모각존이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_EXISTS_HOST_MEMBER("방장이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    FULL_CAPACITY("모각존 정원이 가득 찼습니다.", HttpStatus.BAD_REQUEST),
    INVALID_ZONE_PASSWORD("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    LOGIN_REQUIRED_FOR_JOIN("로그인이 필요합니다.", HttpStatus.BAD_REQUEST),
    ALREADY_JOINED("이미 가입되었습니다.", HttpStatus.BAD_REQUEST),

    // CHALLENGE Exception
    NOT_EXISTS_CHALLENGE("챌린지가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_STARTED("이미 시작된 챌린지는 참여가 불가능합니다.", HttpStatus.BAD_REQUEST),
    INVALID_CHALLENGE_DATE("시작일은 현재시간보다 이전이여야 합니다.", HttpStatus.BAD_REQUEST),

    // CHALLENGE_ARTICLE Exception
    UPLOAD_FAILED("파일 업로드에 실패하였습니다.", HttpStatus.BAD_REQUEST),

    // Member Exception
    NOT_EXISTS_MEMBER("해당 회원이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_ALREADY_DELETED("탈퇴한 회원입니다.", HttpStatus.BAD_REQUEST),

    // S3 Exception
    FILE_UPLOAD_FAILED("파일 업로드에 실패하였습니다.", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE("업로드 가능한 파일 용량을 초과했습니다.", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE("이미지만 업로드 가능합니다.", HttpStatus.BAD_REQUEST),

    // Worry Exception
    NOT_EXISTS_WORRY("해당 고민있어요가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String description;
    private final HttpStatus httpStatus;
}
