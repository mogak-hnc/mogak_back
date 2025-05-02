package com.hnc.mogak.worry.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WorryDetailResponse {

    private String title;
    private String body;
    private Integer empathyCount;
    private List<Integer> restTime;
    private List<CommentResponse> commentResponses;

}
