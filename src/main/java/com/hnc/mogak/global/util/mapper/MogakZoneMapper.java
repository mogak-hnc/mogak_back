package com.hnc.mogak.global.util.mapper;

import com.hnc.mogak.zone.adapter.in.web.dto.CreateMogakZoneResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.MogakZoneEntity;
import com.hnc.mogak.zone.application.port.in.command.CreateMogakZoneCommand;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zone.vo.ZoneConfig;
import com.hnc.mogak.zone.domain.zone.vo.ZoneId;
import com.hnc.mogak.zone.domain.zone.vo.ZoneInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class MogakZoneMapper {

    public static MogakZone mapToDomainWithoutId(CreateMogakZoneCommand command, String imageUrl) {
        ZoneInfo zoneInfo = new ZoneInfo(
                command.getName(),
                imageUrl,
                command.getPassword(),
                0
        );

        ZoneConfig zoneConfig = new ZoneConfig(
                command.isChatEnabled(),
                command.isPasswordRequired(),
                command.getMaxCapacity()
        );

        return MogakZone.withoutId(zoneConfig, zoneInfo);
    }

    public static MogakZone mapToDomainWithId(MogakZoneEntity mogakZoneEntity) {
        ZoneId zoneId = new ZoneId(mogakZoneEntity.getId());

        ZoneInfo zoneInfo = new ZoneInfo(
                mogakZoneEntity.getName(),
                mogakZoneEntity.getImageUrl(),
                mogakZoneEntity.getPassword(),
                mogakZoneEntity.getParticipantNum()
        );

        ZoneConfig zoneConfig = new ZoneConfig(
                mogakZoneEntity.isChatEnabled(),
                mogakZoneEntity.isPasswordRequired(),
                mogakZoneEntity.getMaxCapacity()
        );

        return MogakZone.withId(zoneId, zoneConfig, zoneInfo);
    }

    public static MogakZoneEntity mapToEntity(MogakZone mogakZone) {
        Long id = mogakZone.getZoneId() == null ? null : mogakZone.getZoneId().value();
        ZoneConfig zoneConfig = mogakZone.getZoneConfig();
        ZoneInfo zoneInfo = mogakZone.getZoneInfo();

        return MogakZoneEntity.builder()
                .id(id)
                .name(zoneInfo.name())
                .imageUrl(zoneInfo.imageUrl())
                .password(zoneInfo.password())
                .maxCapacity(zoneConfig.maxCapacity())
                .passwordRequired(zoneConfig.passwordEnabled())
                .chatEnabled(zoneConfig.chatEnabled())
                .participantNum(zoneInfo.participantNum())
                .build();
    }

    public static CreateMogakZoneResponse mapToMogakZoneResponse(MogakZone mogakZone, Set<String> tagNames) {
        return CreateMogakZoneResponse.builder()
                .mogakZoneId(mogakZone.getZoneId().value())
                .name(mogakZone.getZoneInfo().name())
                .maxCapacity(mogakZone.getZoneConfig().maxCapacity())
                .imageUrl(mogakZone.getZoneInfo().imageUrl())
                .password(mogakZone.getZoneInfo().password())
                .chatEnabled(mogakZone.getZoneConfig().chatEnabled())
                .tagNames(tagNames)
                .build();
    }

}
