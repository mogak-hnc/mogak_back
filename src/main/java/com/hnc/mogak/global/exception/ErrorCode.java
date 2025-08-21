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
    NEED_PASSWORD("패스워드를 기입해야합니다.", HttpStatus.BAD_REQUEST),
    NOT_MEMBER("모각존 멤버가 아닙니다.", HttpStatus.BAD_REQUEST),

    // CHALLENGE Exception
    NOT_EXISTS_CHALLENGE("챌린지가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_STARTED("이미 시작된 챌린지는 참여가 불가능합니다.", HttpStatus.BAD_REQUEST),
    INVALID_CHALLENGE_DATE("시작일은 현재시간보다 이전이여야 합니다.", HttpStatus.BAD_REQUEST),
    NOT_CREATOR("방장이 아닙니다.", HttpStatus.BAD_REQUEST),
    NOT_JOINED_CHALLENGE("챌린지에 참가한 회원이 아닙니다.", HttpStatus.BAD_REQUEST),
    CANNOT_KICK_HOST("방장 본인은 내보내지 못합니다.", HttpStatus.BAD_REQUEST),

    // CHALLENGE_ARTICLE Exception
    UPLOAD_FAILED("파일 업로드에 실패하였습니다.", HttpStatus.BAD_REQUEST),
    TOO_MANY_IMAGES("10장 이하의 이미지만 등록가능합니다.", HttpStatus.BAD_REQUEST),
    INVALID_ACCESS("잘못된 접근입니다.", HttpStatus.BAD_REQUEST),
    ONLY_ONE_POST_PER_DAY("하루에 하나만 업로드 가능합니다.", HttpStatus.BAD_REQUEST),
    ONLY_CAN_UPLOAD_WHEN_ONGOING("챌린지 진행 중에만 업로드 할 수 있습니다.", HttpStatus.BAD_REQUEST),
    NOT_SURVIVOR("생존자가 아닙니다.", HttpStatus.BAD_REQUEST),
    CACHED_FAILED("캐시 대기 중 예외발생", HttpStatus.BAD_REQUEST),

    // Member Exception
    NOT_EXISTS_MEMBER("해당 회원이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_ALREADY_DELETED("탈퇴한 회원입니다.", HttpStatus.BAD_REQUEST),
    NICKNAME_ALREADY_EXISTS("이미 사용 중인 닉네임입니다.", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD("잘못된 비밀번호입니다.", HttpStatus.BAD_REQUEST),

    // S3 Exception
    FILE_UPLOAD_FAILED("파일 업로드에 실패하였습니다.", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE("업로드 가능한 파일 용량을 초과했습니다.", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE("이미지만 업로드 가능합니다.", HttpStatus.BAD_REQUEST),

    // Badge Exception
    NOT_EXISTS_BADGE("해당 뱃지가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    ONLY_ACCESS_ADMIN("운영자만 접근할 수 있습니다.", HttpStatus.BAD_REQUEST),

    // Worry Exception
    NOT_EXISTS_WORRY("해당 고민있어요가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_EXISTS_COMMENT("해당 댓글이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String description;
    private final HttpStatus httpStatus;
}
