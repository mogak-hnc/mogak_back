package com.hnc.mogak.zone.adapter.out.persistence;

import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.MogakZoneException;
import com.hnc.mogak.global.util.mapper.MemberMapper;
import com.hnc.mogak.global.util.mapper.MogakZoneMapper;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneSearchResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.MogakZoneEntity;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneOwnerEntity;
import com.hnc.mogak.zone.adapter.out.persistence.repository.MogakZoneQueryDslRepository;
import com.hnc.mogak.zone.adapter.out.persistence.repository.MogakZoneRepository;
import com.hnc.mogak.zone.adapter.out.persistence.repository.ZoneOwnerRepository;
import com.hnc.mogak.zone.adapter.out.persistence.repository.ZoneTagRepository;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import com.hnc.mogak.zone.application.port.out.MogakZoneQueryPort;
import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MogakZoneQueryAdapter implements MogakZoneQueryPort {

    private final ZoneTagRepository zoneTagRepository;
    private final MogakZoneRepository mogakZoneRepository;
    private final ZoneOwnerRepository zoneOwnerRepository;
    private final MogakZoneQueryDslRepository mogakZoneQueryDslRepository;

    @Override
    public List<String> getTags(Long mogakZoneId) {
        return zoneTagRepository.findAllByZoneId(mogakZoneId).stream()
                .map(zoneTagEntity -> zoneTagEntity.getTag().getName())
                .collect(Collectors.toList());
    }

    @Override
    public MogakZone findById(Long mogakZoneId) {
        MogakZoneEntity mogakZoneEntity = mogakZoneRepository.findById(mogakZoneId)
                .orElseThrow(() -> new MogakZoneException(ErrorCode.NOT_EXISTS_MOGAKZONE));
        return MogakZoneMapper.mapToDomainWithId(mogakZoneEntity);
    }

    @Override
    public ZoneOwner findByMogakZoneId(Long mogakZoneId) {
        ZoneOwnerEntity zoneOwnerEntity = zoneOwnerRepository.findByMogakZoneId(mogakZoneId);
        Member member = MemberMapper.mapToDomainEntity(zoneOwnerEntity.getMemberEntity());
        MogakZone mogakZone = MogakZoneMapper.mapToDomainWithId(zoneOwnerEntity.getMogakZoneEntity());
        return ZoneOwner.withId(zoneOwnerEntity.getId(), member, mogakZone);
    }

    @Override
    public Page<MogakZoneSearchResponse> searchMogakZone(MogakZoneSearchQuery query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        return mogakZoneQueryDslRepository.findMogakZone(query, pageable);
    }

}
