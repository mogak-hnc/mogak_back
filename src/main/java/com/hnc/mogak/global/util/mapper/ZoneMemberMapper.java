package com.hnc.mogak.global.util.mapper;

import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneDetailResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneMemberEntity;
import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;
import com.hnc.mogak.zone.domain.zonemember.vo.ZoneMemberId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ZoneMemberMapper {

    public static MogakZoneDetailResponse mapToMogakZoneDetailResponse(
            List<String> tagNames,
            MogakZone mogakZone,
            ZoneOwner zoneOwner,
            List<ZoneMember> zoneMemberList
    ) {
        return MogakZoneDetailResponse.builder()
                .tagNames(tagNames)
                .hostMemberId(zoneOwner.getHostMember().getMemberId().value())
                .name(mogakZone.getZoneInfo().name())
                .startDate(mogakZone.getZoneDuration().startDate())
                .endDate(mogakZone.getZoneDuration().endDate())
                .joinedUserCount(zoneMemberList.size())
                .zoneMemberInfoList(getZoneMemberInfos(zoneMemberList))
                .build();
    }

    private static List<MogakZoneDetailResponse.ZoneMemberInfo> getZoneMemberInfos(List<ZoneMember> zoneMemberList) {
        return zoneMemberList.stream()
                .map(zoneMember ->
                        MogakZoneDetailResponse.ZoneMemberInfo
                                .builder()
                                .memberId(zoneMember.getMember().getMemberId().value())
                                .status(zoneMember.getStatus())
                                .nickname(zoneMember.getMember().getMemberInfo().nickname())
                                .build()
                )
                .toList();
    }

    public static ZoneMember mapToDomain(ZoneMemberEntity zoneMemberEntity, Member member, MogakZone mogakZone) {
        ZoneMemberId zoneMemberId = new ZoneMemberId(zoneMemberEntity.getId());
        return ZoneMember.withId(zoneMemberId, zoneMemberEntity.getStatus(), member, mogakZone);
    }

}
