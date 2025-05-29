package com.hnc.mogak.worry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorryCommentResponse {

    private Long memberId;
    private Integer commentId;
    private String comment;
    private LocalDateTime createdAt;
    private String nickname;
    private String profileImageUrl;

}