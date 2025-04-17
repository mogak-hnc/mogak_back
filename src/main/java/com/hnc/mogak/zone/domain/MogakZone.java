package com.hnc.mogak.zone.domain;

import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.domain.vo.ZoneConfig;
import com.hnc.mogak.zone.domain.vo.ZoneDuration;
import com.hnc.mogak.zone.domain.vo.ZoneId;
import com.hnc.mogak.zone.domain.vo.ZoneInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MogakZone {

    private ZoneId zoneId;
    private ZoneInfo zoneInfo;
    private ZoneConfig zoneConfig;
    private ZoneDuration zoneDuration;
    private Member hostMember;

    public static MogakZone withId(
            ZoneId zoneId,
            ZoneDuration zoneDuration,
            ZoneConfig zoneConfig,
            ZoneInfo zoneInfo,
            Member member
    ) {
        return MogakZone.builder()
                .zoneId(zoneId)
                .zoneDuration(zoneDuration)
                .zoneConfig(zoneConfig)
                .zoneInfo(zoneInfo)
                .hostMember(member)
                .build();
    }

    public static MogakZone withoutId(
            ZoneDuration zoneDuration,
            ZoneConfig zoneConfig,
            ZoneInfo zoneInfo,
            Member member
    ) {
        return MogakZone.builder()
                .zoneId(null)
                .zoneDuration(zoneDuration)
                .zoneConfig(zoneConfig)
                .zoneInfo(zoneInfo)
                .hostMember(member)
                .build();
    }

}