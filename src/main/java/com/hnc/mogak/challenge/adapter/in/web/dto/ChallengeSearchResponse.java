package com.hnc.mogak.challenge.adapter.in.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ChallengeSearchResponse {

    private boolean official;
    private String title;
    private List<String> memberImageUrl;
    private LocalDate startDate;
    private LocalDate endDate;

}
