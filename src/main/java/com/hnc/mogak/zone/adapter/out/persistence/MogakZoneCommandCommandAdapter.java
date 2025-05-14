package com.hnc.mogak.zone.adapter.out.persistence;

import com.hnc.mogak.global.util.mapper.MemberMapper;
import com.hnc.mogak.global.util.mapper.MogakZoneMapper;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.out.persistence.entity.*;
import com.hnc.mogak.zone.adapter.out.persistence.repository.*;
import com.hnc.mogak.zone.application.port.out.MogakZoneCommandPort;
import com.hnc.mogak.zone.application.port.out.TagPort;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MogakZoneCommandCommandAdapter implements MogakZoneCommandPort, TagPort {

    private final MogakZoneRepository mogakZoneRepository;
    private final ZoneOwnerRepository zoneOwnerRepository;
    private final ZoneTagRepository zoneTagRepository;
    private final TagRepository tagRepository;
    private final ZoneSummaryRepository zoneSummaryRepository;

//    @Override
//    public CreateMogakZoneResponse createMogakZone(MogakZone mogakZone, Set<TagEntity> tagSet) {
//        MogakZoneEntity mogakZoneEntity = mogakZoneRepository.save(mogakZoneMapper.mapToEntity(mogakZone));
//        tagSet.forEach(tagEntity -> zoneTagRepository.save(new ZoneTagEntity(null, tagEntity, mogakZoneEntity)));
//
//        Set<String> tagNames = tagSet.stream().map(TagEntity::getName).collect(Collectors.toSet());
//        return mogakZoneMapper.mapToMogakZoneResponse(mogakZoneEntity, tagNames);
//    }

    @Override
    public void saveZoneSummary(MogakZone mogakZone, Set<TagEntity> tagEntitySet) {
        StringBuilder sb = new StringBuilder();
        for (TagEntity tagEntity : tagEntitySet) {
            sb.append(tagEntity.getName()).append(" ");
        }

        String tagNames = sb.toString().trim();
        String memberUrl = "";

        zoneSummaryRepository.save(
                new ZoneSummary(
                        null,
                        mogakZone.getZoneId().value(),
                        tagNames,
                        mogakZone.getZoneInfo().name(),
                        0L,
                        memberUrl
                )
        );
    }

    @Override
    public MogakZone createMogakZone(MogakZone mogakZone, Set<TagEntity> tagSet) {
        MogakZoneEntity mogakZoneEntity = mogakZoneRepository.save(MogakZoneMapper.mapToEntity(mogakZone));
        tagSet.forEach(tagEntity -> zoneTagRepository.save(new ZoneTagEntity(null, tagEntity, mogakZoneEntity)));
        return MogakZoneMapper.mapToDomainWithId(mogakZoneEntity);
    }

    @Override
    public void saveMogakZone(MogakZone mogakZone) {
        mogakZoneRepository.save(MogakZoneMapper.mapToEntity(mogakZone));
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

    @Override
    public List<String> findTagNameByMogakZoneId(Long mogakZoneId) {
        return zoneTagRepository.findTagNamesByMogakZoneId(mogakZoneId);
    }
}
