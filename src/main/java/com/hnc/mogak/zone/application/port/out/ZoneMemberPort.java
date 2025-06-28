package com.hnc.mogak.zone.application.port.out;

import com.hnc.mogak.member.adapter.in.web.dto.ZoneInfoResponse;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.in.web.dto.JoinMogakZoneResponse;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;
import com.hnc.mogak.zone.domain.zonemember.vo.ZoneMemberStatus;

import java.util.List;

public interface ZoneMemberPort {

    List<ZoneMember> findAllZoneMembersWithMembersByMogakZoneId(Long mogakZoneId);

    JoinMogakZoneResponse join(MogakZone mogakZone, Member findMember);

    int getZoneMemberCount(Long mogakZoneId);

    void deleteMemberByMogakZoneId(Long mogakZoneId, Long memberId);

    void changeStatus(Long memberId, Long mogakZoneId, ZoneMemberStatus status);

    boolean isMemberInMogakZone(Long mogakZoneId, Long memberId);

    List<ZoneInfoResponse> findJoinedZones(Long memberId);

}
