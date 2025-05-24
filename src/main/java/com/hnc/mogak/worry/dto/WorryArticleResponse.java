package com.hnc.mogak.worry.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WorryArticleResponse {

    private String title;
    private String body;
    private Integer empathyCount;
    private List<Integer> restTime;
    private boolean hasEmpathized;

}
