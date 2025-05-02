package com.hnc.mogak.worry.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class WorryPreview {

    private String title;
    private int commentCount;
    private Integer worryId;
    private List<Integer> restTime;

}
