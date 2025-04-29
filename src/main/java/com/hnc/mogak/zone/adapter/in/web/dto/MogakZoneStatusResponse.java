package com.hnc.mogak.zone.adapter.in.web.dto;

import com.hnc.mogak.zone.domain.zonemember.vo.ZoneMemberStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MogakZoneStatusResponse {

    private ZoneMemberStatus status;
    private Long memberId;
    private Long mogakZoneId;

}
