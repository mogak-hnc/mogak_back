package com.hnc.mogak.zone.domain.zone;

import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.MogakZoneException;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.domain.zone.vo.ZoneConfig;
import com.hnc.mogak.zone.domain.zone.vo.ZoneDuration;
import com.hnc.mogak.zone.domain.zone.vo.ZoneId;
import com.hnc.mogak.zone.domain.zone.vo.ZoneInfo;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MogakZone {

    private ZoneId zoneId;
    private ZoneInfo zoneInfo;
    private ZoneConfig zoneConfig;
    private ZoneDuration zoneDuration;

    public static MogakZone withId(
            ZoneId zoneId,
            ZoneDuration zoneDuration,
            ZoneConfig zoneConfig,
            ZoneInfo zoneInfo
    ) {
        return MogakZone.builder()
                .zoneId(zoneId)
                .zoneDuration(zoneDuration)
                .zoneConfig(zoneConfig)
                .zoneInfo(zoneInfo)
                .build();
    }

    public static MogakZone withoutId(
            ZoneDuration zoneDuration,
            ZoneConfig zoneConfig,
            ZoneInfo zoneInfo
    ) {
        return MogakZone.builder()
                .zoneId(null)
                .zoneDuration(zoneDuration)
                .zoneConfig(zoneConfig)
                .zoneInfo(zoneInfo)
                .build();
    }

    public boolean isCapacityAvailableForJoin(int maxCapacity, int currentMemberCount) {
        return maxCapacity <= currentMemberCount;
    }

    public boolean isLoginRequired(boolean isLoginRequired, Long memberId) {
        if (isLoginRequired && memberId == null) {
            return true;
        }

        return false;
    }

    public boolean isAlreadyJoined(Long memberId, List<ZoneMember> zoneMemberList) {
        for (ZoneMember zoneMember : zoneMemberList) {
            if (Objects.equals(zoneMember.getMember().getMemberId().value(), memberId)) {
                return true;
            }
        }

        return false;
    }

    public boolean isMatchPassword(String actualPassword, String inputPassword) {
        return actualPassword.equals(inputPassword);
    }

}