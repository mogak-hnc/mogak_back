package com.hnc.mogak.zone.adapter.out.persistence;

import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.MogakZoneException;
import com.hnc.mogak.global.util.mapper.MemberMapper;
import com.hnc.mogak.global.util.mapper.MogakZoneMapper;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneSearchResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.TagNameResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.MogakZoneEntity;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneOwnerEntity;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneSummary;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneSummaryMemberImage;
import com.hnc.mogak.zone.adapter.out.persistence.repository.*;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import com.hnc.mogak.zone.application.port.out.MogakZoneQueryPort;
import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MogakZoneQueryAdapter implements MogakZoneQueryPort {

    private final ZoneTagRepository zoneTagRepository;
    private final MogakZoneRepository mogakZoneRepository;
    private final ZoneOwnerRepository zoneOwnerRepository;
    private final ZoneSummaryRepository zoneSummaryRepository;
    private final ZoneSummaryMemberImageRepository zoneSummaryMemberImageRepository;
    private final MogakZoneQueryDslRepository mogakZoneQueryDslRepository;

    @Override
    public List<String> getTags(Long mogakZoneId) {
        return zoneTagRepository.findAllByZoneId(mogakZoneId).stream()
                .map(zoneTagEntity -> zoneTagEntity.getTag().getName())
                .collect(Collectors.toList());
    }

    @Override
    public List<ZoneSummary> findTopZoneSummariesByJoinCount(int size) {
        Pageable pageable = PageRequest.of(0, size);
        return zoneSummaryRepository.findAllByOrderByParticipantNumDesc(pageable);
    }

    @Override
    public ZoneSummary getSummaryDetail(Long mogakZoneId) {
        return zoneSummaryRepository.findByMogakZoneId(mogakZoneId);
    }

    @Override
    public MogakZone findById(Long mogakZoneId) {
        MogakZoneEntity mogakZoneEntity = mogakZoneRepository.findById(mogakZoneId)
                .orElseThrow(() -> new MogakZoneException(ErrorCode.NOT_EXISTS_MOGAKZONE));
        return MogakZoneMapper.mapToDomainWithId(mogakZoneEntity);
    }

    @Override
    public ZoneOwner findZoneOwnerByMogakZoneId(Long mogakZoneId) {
        ZoneOwnerEntity zoneOwnerEntity = zoneOwnerRepository.findByMogakZoneId(mogakZoneId);
        Member member = MemberMapper.mapToDomainEntity(zoneOwnerEntity.getMemberEntity());
        MogakZone mogakZone = MogakZoneMapper.mapToDomainWithId(zoneOwnerEntity.getMogakZoneEntity());
        return ZoneOwner.withId(zoneOwnerEntity.getId(), member, mogakZone);
    }

    //=====================================//
//    @Override
//    public Page<MogakZoneSearchResponse> searchMogakZone(MogakZoneSearchQuery query) {
//        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
//        return mogakZoneQueryDslRepository.findMogakZone(query, pageable);
//    }
//
    @Override
    public Page<MogakZoneSearchResponse> searchMogakZone(MogakZoneSearchQuery query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        return mogakZoneQueryDslRepository.findMogakZone(query, pageable);
    }



    @Override
    public List<TagNameResponse> getPopularTags() {
        Pageable pageable = PageRequest.of(0, 5);
        List<String> tagNames = zoneTagRepository.getPopularTagNames(pageable);
        return tagNames.stream().map(TagNameResponse::new).toList();
    }

    @Override
    public Map<Long, List<String>> getZoneMemberImagesByZoneIds(List<Long> zoneIds, int size) {
        List<Object[]> results = zoneSummaryMemberImageRepository.findTopImagesByZoneIds(zoneIds, size);
        Map<Long, List<String>> map = new HashMap<>();
        for (Object[] row : results) {
            Long zoneId = ((Number) row[0]).longValue();
            String imageUrl = (String) row[1];

            map.computeIfAbsent(zoneId, k -> new ArrayList<>()).add(imageUrl);
        }

        return map;
    }

    @Override
    public void saveZoneSummaryMemberImage(Long mogakZoneId, Long memberId, String memberImageUrl) {
        zoneSummaryMemberImageRepository.save(new ZoneSummaryMemberImage(null, mogakZoneId, memberId, memberImageUrl));
    }

    @Override
    public Long findZoneOwnerIdByMogakZoneId(Long mogakZoneId) {
        return zoneOwnerRepository.findZoneOwnerIdByMogakZoneId(mogakZoneId);
    }

    @Override
    public MogakZone findWithLock(Long mogakZoneId) {
        MogakZoneEntity mogakZoneEntity = mogakZoneRepository.findByIdWithLock(mogakZoneId)
                .orElseThrow(() -> new MogakZoneException(ErrorCode.NOT_EXISTS_MOGAKZONE));
        return MogakZoneMapper.mapToDomainWithId(mogakZoneEntity);
    }

    @Override
    public ZoneSummary findSummaryWithLock(Long value) {
        return zoneSummaryRepository.findByIdWithLock(value)
                .orElseThrow(() -> new MogakZoneException(ErrorCode.NOT_EXISTS_MOGAKZONE));
    }
}