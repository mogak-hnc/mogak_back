package com.hnc.mogak.challenge.adapter.in.web.dto;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MogakChallengeMainResponse {

    private Long challengeId;
    private boolean official;
    private String title;
    List<String> memberImageUrls;
    private LocalDate startDate;
    private LocalDate endDate;
    private ChallengeStatus status;

}