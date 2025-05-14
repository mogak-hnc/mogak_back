package com.hnc.mogak.zone.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateMogakZoneRequest {

    @NotNull
    @Schema(description = "모각존 이름", example = "같이 공부할 사람")
    private String name;

    @NotNull
    @Schema(description = "모각존 태그", example = "혼공")
    private String tag;

    @Min(1)
    @Schema(description = "최대 참여 인원", example = "5")
    private int maxCapacity;

    @Schema(description = "비밀번호 (비공개 모각존일 경우)", example = "")
    private String password;

    @Schema(description = "채팅 기능 사용 여부", example = "false")
    private boolean chatEnabled;

    @Schema(description = "로그인 필수 여부", example = "false")
    private boolean loginRequired;

    @Schema(description = "모각존 운영 기간(예제 형식에 맞춰야합니다.)", example = "2025-04-18~2025-04-25")
    private String period;

}