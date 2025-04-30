package com.hnc.mogak.challenge.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateChallengeRequest {

    @Schema(description = "챌린지 제목", example = "매일 아침 6시 기상 챌린지")
    @NotBlank
    private String title;

    @Schema(description = "챌린지 설명", example = "20일간 매일 아침 6시에 기상하는 챌린지입니다.")
    @NotBlank
    private String description;

    @Schema(
            description = "챌린지 기간 (시작일~종료일 형식의 문자열)",
            example = "2025-05-01~2025-05-20"
    )

    @NotBlank
    private String period;
}