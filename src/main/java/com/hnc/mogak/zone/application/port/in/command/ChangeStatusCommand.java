package com.hnc.mogak.zone.application.port.in.command;

import com.hnc.mogak.zone.domain.zonemember.vo.ZoneMemberStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangeStatusCommand {

    private Long mogakZoneId;
    private ZoneMemberStatus status;
    private Long memberId;

}
