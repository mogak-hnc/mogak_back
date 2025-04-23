package com.hnc.mogak.zone.domain.zone;

import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.MogakZoneException;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.domain.zone.vo.ZoneConfig;
import com.hnc.mogak.zone.domain.zone.vo.ZoneDuration;
import com.hnc.mogak.zone.domain.zone.vo.ZoneId;
import com.hnc.mogak.zone.domain.zone.vo.ZoneInfo;
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

    public void validateJoinable(int maxCapacity, int currentMemberCount) {
        if (maxCapacity <= currentMemberCount) {
            throw new MogakZoneException(ErrorCode.FULL_CAPACITY);
        }
    }

    public void validatePassword(String actualPassword, String inputPassword) {
        if (!actualPassword.equals(inputPassword)) {
            throw new MogakZoneException(ErrorCode.INVALID_ZONE_PASSWORD);
        }
    }

    public void validateLoginRequired(boolean isLoginRequired, Long memberId) {
        if (isLoginRequired && memberId == null) {
            throw new MogakZoneException(ErrorCode.LOGIN_REQUIRED_FOR_JOIN);
        }
    }

}