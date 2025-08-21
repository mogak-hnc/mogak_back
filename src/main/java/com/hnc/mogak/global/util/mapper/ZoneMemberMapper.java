package com.hnc.mogak.global.util.mapper;

import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.in.web.dto.ChatMessageResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneCommonData;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneDetailResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.SendJoinMogakZoneResponse;
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
            MogakZoneCommonData data,
            boolean isJoined
    ) {

        return MogakZoneDetailResponse.builder()
                .tagNames(data.getTagNames())
                .hostMemberId(data.getZoneOwner().getHostMember().getMemberId().value())
                .name(data.getMogakZone().getZoneInfo().name())
                .joinedUserCount(data.getZoneMemberList().size())
                .imageUrl(data.getMogakZone().getZoneInfo().imageUrl())
                .zoneMemberInfoList(getZoneMemberInfos(data.getZoneMemberList()))
                .isJoined(isJoined)
                .passwordRequired(data.isPasswordEnabled())
                .maxCapacity(data.getMogakZone().getZoneConfig().maxCapacity())
                .build();
    }

    private static List<MogakZoneDetailResponse.ZoneMemberInfo> getZoneMemberInfos(List<ZoneMember> zoneMemberList) {
        return zoneMemberList.stream()
                .map(zoneMember ->
                        MogakZoneDetailResponse.ZoneMemberInfo
                                .builder()
                                .memberId(zoneMember.getMember().getMemberId().value())
                                .nickname(zoneMember.getMember().getMemberInfo().nickname())
                                .imageUrl(zoneMember.getMember().getMemberInfo().imagePath())
                                .status(zoneMember.getStatus())
                                .build()
                )
                .toList();
    }

    public static ZoneMember mapToDomain(ZoneMemberEntity zoneMemberEntity, Member member, MogakZone mogakZone) {
        ZoneMemberId zoneMemberId = new ZoneMemberId(zoneMemberEntity.getId());
        return ZoneMember.withId(zoneMemberId, zoneMemberEntity.getStatus(), member, mogakZone);
    }

    public static SendJoinMogakZoneResponse mapToSendJoinMogakZoneResponse(List<ZoneMember> zoneMemberList) {
        List<MogakZoneDetailResponse.ZoneMemberInfo> zoneMemberInfos = getZoneMemberInfos(zoneMemberList);
        return SendJoinMogakZoneResponse.builder()
                .zoneMemberInfoList(zoneMemberInfos)
                .joinedUserCount(zoneMemberList.size())
                .build();
    }
}
