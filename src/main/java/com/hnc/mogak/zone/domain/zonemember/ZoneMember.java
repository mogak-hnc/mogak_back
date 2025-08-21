package com.hnc.mogak.zone.domain.zonemember;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zonemember.vo.ZoneMemberId;
import com.hnc.mogak.zone.domain.zonemember.vo.ZoneMemberStatus;
import lombok.*;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
