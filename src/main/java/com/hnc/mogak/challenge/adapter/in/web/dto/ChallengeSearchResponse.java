package com.hnc.mogak.challenge.adapter.in.web.dto;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeSearchResponse {

    private Long challengeId;
    private Boolean official;
    private String title;
    private List<String> memberImageUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private ChallengeStatus status;

}
