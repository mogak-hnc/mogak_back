package com.hnc.mogak.zone.adapter.in.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MogakZoneRequest {

    @NotNull
    private String name;

    @NotNull
    private String tag;

    @Min(1)
    private int maxCapacity;

    private String imageUrl;

    private String password;

    private boolean chatEnabled;
    private boolean loginRequired;

    private String period;

}