package com.hnc.mogak.zone.application.port.out;

import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.in.web.dto.JoinResponse;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;

import java.util.List;

public interface ZoneMemberPort {

    List<ZoneMember> findAllZoneMembersByMogakZoneId(Long mogakZoneId);

    JoinResponse join(MogakZone mogakZone, Member findMember);

    int getZoneMemberCount(Long mogakZoneId);

}
