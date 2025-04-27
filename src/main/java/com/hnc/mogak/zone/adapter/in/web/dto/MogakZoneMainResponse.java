package com.hnc.mogak.zone.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MogakZoneMainResponse {

    List<String> tagNames;
    private String name;
    List<String> memberImageUrls;

}