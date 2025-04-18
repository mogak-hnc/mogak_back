package com.hnc.mogak.global.util.mapper;

import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.MogakZoneEntity;
import com.hnc.mogak.zone.application.port.in.command.CreateMogakZoneCommand;
import com.hnc.mogak.zone.domain.MogakZone;
import com.hnc.mogak.zone.domain.vo.ZoneConfig;
import com.hnc.mogak.zone.domain.vo.ZoneDuration;
import com.hnc.mogak.zone.domain.vo.ZoneId;
import com.hnc.mogak.zone.domain.vo.ZoneInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class MogakZoneMapper {

    public MogakZone mapToDomain(CreateMogakZoneCommand command, Member hostMember) {
        ZoneInfo zoneInfo = new ZoneInfo(
                command.getName(),
                command.getMaxCapacity(),
                command.getImageUrl(),
                command.getPassword()
        );

        ZoneConfig zoneConfig = new ZoneConfig(
                command.isLoginRequired(),
                command.isChatEnabled()
        );

        ZoneDuration zoneDuration = new ZoneDuration(
                command.getStartDate(), command.getEndDate()
        );

        return MogakZone.withoutId(zoneDuration, zoneConfig, zoneInfo, hostMember);
    }

    public MogakZoneEntity mapToEntity(MogakZone mogakZone, MemberEntity memberEntity) {
        ZoneId zoneId = mogakZone.getZoneId();
        ZoneConfig zoneConfig = mogakZone.getZoneConfig();
        ZoneDuration zoneDuration = mogakZone.getZoneDuration();
        ZoneInfo zoneInfo = mogakZone.getZoneInfo();

        return MogakZoneEntity.builder()
                .zoneId(null)
                .name(zoneInfo.name())
                .maxCapacity(zoneInfo.maxCapacity())
                .imageUrl(zoneInfo.imageUrl())
                .password(zoneInfo.password())
                .loginRequired(zoneConfig.loginRequired())
                .chatEnabled(zoneConfig.chatEnabled())
                .startDate(zoneDuration.startDate())
                .endDate(zoneDuration.endDate())
                .hostMemberEntity(memberEntity)
                .build();
    }

    public MogakZoneResponse mapToMogakZoneResponse(MogakZoneEntity mogakZoneEntity, Set<String> tagNames) {
        return MogakZoneResponse.builder()
                .mogakZoneId(mogakZoneEntity.getZoneId())
                .name(mogakZoneEntity.getName())
                .maxCapacity(mogakZoneEntity.getMaxCapacity())
                .imageUrl(mogakZoneEntity.getImageUrl())
                .password(mogakZoneEntity.getPassword())
                .chatEnabled(mogakZoneEntity.isChatEnabled())
                .loginRequired(mogakZoneEntity.isLoginRequired())
                .startDate(mogakZoneEntity.getStartDate())
                .endDate(mogakZoneEntity.getEndDate())
                .tagNames(tagNames)
                .build();
    }

}
