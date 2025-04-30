package com.hnc.mogak.zone.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class JoinMogakZoneRequest {

    @Schema(description = "모각존 비밀번호 (필요한 경우 입력)", example = "")
    private String password;

}
