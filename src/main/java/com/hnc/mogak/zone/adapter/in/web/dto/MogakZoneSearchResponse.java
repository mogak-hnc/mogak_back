package com.hnc.mogak.zone.adapter.in.web.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MogakZoneSearchResponse {

    private Long mogakZoneId;
    private String name;
    private boolean passwordRequired;

    private List<String> tagNames;
    private List<String> memberImageUrls;

}