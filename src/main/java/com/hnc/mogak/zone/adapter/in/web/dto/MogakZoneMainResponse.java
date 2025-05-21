package com.hnc.mogak.zone.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MogakZoneMainResponse {

    private Long mogakZoneId;
    private List<String> tagNames;
    private String name;
    private List<String> memberImageUrls;
    private boolean passwordRequired;

}