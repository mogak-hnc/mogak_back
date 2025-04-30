package com.hnc.mogak.challenge.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CreateChallengeArticleRequest {

    @Schema(description = "게시글 본문", example = "오늘의 챌린지를 완료했습니다!")
    private String description;

}