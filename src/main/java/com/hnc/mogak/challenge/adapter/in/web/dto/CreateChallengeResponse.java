package com.hnc.mogak.challenge.adapter.in.web.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateChallengeResponse {

    private Long challengeId;
    private String title;
    private String description;
    private Long creatorMemberId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean official;

}