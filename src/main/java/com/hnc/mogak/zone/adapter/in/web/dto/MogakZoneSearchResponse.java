package com.hnc.mogak.zone.adapter.in.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MogakZoneSearchResponse {

    private Long mogakZoneId;
    private String name;
    private List<String> tagNames;
    private List<String> memberImageUrls;

}
