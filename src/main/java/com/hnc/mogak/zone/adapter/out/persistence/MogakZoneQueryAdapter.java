package com.hnc.mogak.zone.adapter.out.persistence;

import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.MogakZoneException;
import com.hnc.mogak.global.util.mapper.MogakZoneMapper;
import com.hnc.mogak.zone.adapter.out.persistence.entity.MogakZoneEntity;
import com.hnc.mogak.zone.adapter.out.persistence.repository.MogakZoneRepository;
import com.hnc.mogak.zone.adapter.out.persistence.repository.ZoneTagRepository;
import com.hnc.mogak.zone.application.port.out.MogakZoneQueryPort;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MogakZoneQueryAdapter implements MogakZoneQueryPort {

    private final ZoneTagRepository zoneTagRepository;
    private final MogakZoneRepository mogakZoneRepository;

    private final MogakZoneMapper mogakZoneMapper;

    @Override
    public List<String> getTags(Long mogakZoneId) {
        List<String> collect = zoneTagRepository.findAllByZoneId(mogakZoneId).stream()
                .map(zoneTagEntity -> zoneTagEntity.getTag().getName())
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public MogakZone findById(Long mogakZoneId) {
        MogakZoneEntity mogakZoneEntity = mogakZoneRepository.findById(mogakZoneId)
                .orElseThrow(() -> new MogakZoneException(ErrorCode.NOT_EXISTS_MOGAKZONE));
        return mogakZoneMapper.mapToDomainWithId(mogakZoneEntity);
    }

}
