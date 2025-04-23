package com.hnc.mogak.zone.application.port.in.query;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetMogakZoneDetailQuery {

    private String nickname;
    private Long mogakZoneId;
    private Long memberId;

}