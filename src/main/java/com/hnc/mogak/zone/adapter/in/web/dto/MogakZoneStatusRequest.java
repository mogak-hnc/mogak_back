package com.hnc.mogak.zone.adapter.in.web.dto;

import com.hnc.mogak.zone.domain.zonemember.vo.ZoneMemberStatus;
import lombok.Getter;

@Getter
public class MogakZoneStatusRequest {

    private ZoneMemberStatus status;
    private Long memberId;

}
