package com.hnc.mogak.zone.adapter.out.persistence;

import com.hnc.mogak.global.util.mapper.MemberMapper;
import com.hnc.mogak.global.util.mapper.MogakZoneMapper;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.in.web.dto.CreateMogakZoneResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.MogakZoneEntity;
import com.hnc.mogak.zone.adapter.out.persistence.entity.TagEntity;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneOwnerEntity;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneTagEntity;
import com.hnc.mogak.zone.adapter.out.persistence.repository.MogakZoneRepository;
import com.hnc.mogak.zone.adapter.out.persistence.repository.TagRepository;
import com.hnc.mogak.zone.adapter.out.persistence.repository.ZoneOwnerRepository;
import com.hnc.mogak.zone.adapter.out.persistence.repository.ZoneTagRepository;
import com.hnc.mogak.zone.application.port.out.MogakZoneCommandPort;
import com.hnc.mogak.zone.application.port.out.TagPort;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MogakZoneCommandCommandAdapter implements MogakZoneCommandPort, TagPort {

    private final MogakZoneRepository mogakZoneRepository;
    private final ZoneOwnerRepository zoneOwnerRepository;
    private final ZoneTagRepository zoneTagRepository;
    private final TagRepository tagRepository;

//    @Override
//    public CreateMogakZoneResponse createMogakZone(MogakZone mogakZone, Set<TagEntity> tagSet) {
//        MogakZoneEntity mogakZoneEntity = mogakZoneRepository.save(mogakZoneMapper.mapToEntity(mogakZone));
//        tagSet.forEach(tagEntity -> zoneTagRepository.save(new ZoneTagEntity(null, tagEntity, mogakZoneEntity)));
//
//        Set<String> tagNames = tagSet.stream().map(TagEntity::getName).collect(Collectors.toSet());
//        return mogakZoneMapper.mapToMogakZoneResponse(mogakZoneEntity, tagNames);
//    }


    @Override
    public MogakZone createMogakZone(MogakZone mogakZone, Set<TagEntity> tagSet) {
        MogakZoneEntity mogakZoneEntity = mogakZoneRepository.save(MogakZoneMapper.mapToEntity(mogakZone));
        tagSet.forEach(tagEntity -> zoneTagRepository.save(new ZoneTagEntity(null, tagEntity, mogakZoneEntity)));
        return MogakZoneMapper.mapToDomainWithId(mogakZoneEntity);
    }

    @Override
    public Set<TagEntity> findOrCreateTags(Set<String> tagList) {
        return tagList.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(new TagEntity(null, name))))
                .collect(Collectors.toSet());
    }

    @Override
    public void saveZoneOwner(Member member, MogakZone mogakZone) {
        MemberEntity memberEntity = MemberMapper.mapToJpaEntity(member);
        MogakZoneEntity mogakZoneEntity = MogakZoneMapper.mapToEntity(mogakZone);
        zoneOwnerRepository.save(
                ZoneOwnerEntity.builder()
                        .memberEntity(memberEntity)
                        .mogakZoneEntity(mogakZoneEntity)
                        .build()
        );
    }

}
