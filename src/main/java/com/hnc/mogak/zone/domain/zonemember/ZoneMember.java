package com.hnc.mogak.zone.domain.zonemember;

import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zone.vo.ZoneConfig;
import com.hnc.mogak.zone.domain.zone.vo.ZoneDuration;
import com.hnc.mogak.zone.domain.zone.vo.ZoneId;
import com.hnc.mogak.zone.domain.zone.vo.ZoneInfo;
import com.hnc.mogak.zone.domain.zonemember.vo.ZoneMemberId;
import com.hnc.mogak.zone.domain.zonemember.vo.ZoneMemberStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ZoneMember {

    private ZoneMemberId zoneMemberId;
    private ZoneMemberStatus status;
    private Member member;
    private MogakZone mogakZone;

    public static ZoneMember withId(
            ZoneMemberId zoneMemberId,
            ZoneMemberStatus zoneMemberStatus,
            Member member,
            MogakZone mogakZone
    ) {

        return ZoneMember.builder()
                .zoneMemberId(zoneMemberId)
                .status(zoneMemberStatus)
                .member(member)
                .mogakZone(mogakZone)
                .build();

    }

    public static ZoneMember withoutId(
            ZoneMemberStatus zoneMemberStatus,
            Member member,
            MogakZone mogakZone
    ) {

        return ZoneMember.builder()
                .status(zoneMemberStatus)
                .member(member)
                .mogakZone(mogakZone)
                .build();

    }

}
