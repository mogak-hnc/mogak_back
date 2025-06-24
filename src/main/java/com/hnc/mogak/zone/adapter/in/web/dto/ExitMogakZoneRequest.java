package com.hnc.mogak.zone.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ExitMogakZoneRequest {

    @NotNull
    private Long mogakZoneId;

    @NotNull
    private Long memberId;

}