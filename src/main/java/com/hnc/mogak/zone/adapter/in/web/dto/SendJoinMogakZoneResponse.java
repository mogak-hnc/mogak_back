package com.hnc.mogak.zone.adapter.in.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SendJoinMogakZoneResponse {

    private List<MogakZoneDetailResponse.ZoneMemberInfo> zoneMemberInfoList;
    private int joinedUserCount;

}
