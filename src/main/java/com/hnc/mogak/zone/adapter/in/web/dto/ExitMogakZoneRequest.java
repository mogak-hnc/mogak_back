package com.hnc.mogak.zone.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ExitMogakZoneRequest {

    @NotBlank
    private Long mogakZoneId;

    @NotBlank
    private Long memberId;

}
