package com.hnc.mogak.zone.adapter.out.persistence;

import com.hnc.mogak.global.util.mapper.MemberMapper;
import com.hnc.mogak.global.util.mapper.MogakZoneMapper;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import com.hnc.mogak.member.adapter.out.persistence.MemberRepository;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.out.persistence.entity.MogakZoneEntity;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneOwnerEntity;
import com.hnc.mogak.zone.adapter.out.persistence.repository.ZoneOwnerRepository;
import com.hnc.mogak.zone.application.port.out.ZoneOwnerPort;
import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ZoneOwnerAdapter implements ZoneOwnerPort {

    private final ZoneOwnerRepository zoneOwnerRepository;
    private final MemberRepository memberRepository;

    private final MemberMapper memberMapper;
    private final MogakZoneMapper mogakZoneMapper;

    @Override
    public void saveZoneOwner(Member member, MogakZone mogakZone) {
        MemberEntity memberEntity = memberMapper.mapToJpaEntity(member);
        MogakZoneEntity mogakZoneEntity = mogakZoneMapper.mapToEntity(mogakZone);
        zoneOwnerRepository.save(
                ZoneOwnerEntity.builder()
                        .memberEntity(memberEntity)
                        .mogakZoneEntity(mogakZoneEntity)
                        .build()
        );
    }

    @Override
    public ZoneOwner findByMogakZoneId(Long mogakZoneId) {
        ZoneOwnerEntity zoneOwnerEntity = zoneOwnerRepository.findByMogakZoneId(mogakZoneId);
        Member member = memberMapper.mapToDomainEntity(zoneOwnerEntity.getMemberEntity());
        MogakZone mogakZone = mogakZoneMapper.mapToDomainWithId(zoneOwnerEntity.getMogakZoneEntity());
        return ZoneOwner.withId(zoneOwnerEntity.getId(), member, mogakZone);
    }

}
