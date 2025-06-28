package com.hnc.mogak.member.adapter.in.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ZoneInfoResponse {

    private Long zoneId;
    private String name;

}
