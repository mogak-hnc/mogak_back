package com.hnc.mogak.zone.adapter.out.persistence;

import com.hnc.mogak.global.util.mapper.MemberMapper;
import com.hnc.mogak.global.util.mapper.MogakZoneMapper;
import com.hnc.mogak.global.util.mapper.ZoneMemberMapper;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.in.web.dto.JoinResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.MogakZoneEntity;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneMemberEntity;
import com.hnc.mogak.zone.adapter.out.persistence.repository.ZoneMemberRepository;
import com.hnc.mogak.zone.application.port.out.ZoneMemberPort;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;
import com.hnc.mogak.zone.domain.zonemember.vo.ZoneMemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ZoneMemberAdapter implements ZoneMemberPort {

    private final ZoneMemberRepository zoneMemberRepository;

    private final MemberMapper memberMapper;
    private final MogakZoneMapper mogakZoneMapper;
    private final ZoneMemberMapper zoneMemberMapper;

    @Override
    public List<ZoneMember> findAllZoneMembersByMogakZoneId(Long mogakZoneId) {
        List<ZoneMemberEntity> zoneMemberEntityList = zoneMemberRepository.findAllByMogakZoneId(mogakZoneId);
        return zoneMemberEntityList.stream()
                .map(zoneMemberEntity -> {
                    Member member = memberMapper.mapToDomainEntity(zoneMemberEntity.getMemberEntity());
                    MogakZone mogakZone = mogakZoneMapper.mapToDomainWithId(zoneMemberEntity.getMogakZoneEntity());
                    return zoneMemberMapper.mapToDomain(zoneMemberEntity, member, mogakZone);
                })
                .collect(Collectors.toList());
    }

    @Override
    public JoinResponse join(MogakZone mogakZone, Member findMember) {
        MogakZoneEntity mogakZoneEntity = mogakZoneMapper.mapToEntity(mogakZone);
        MemberEntity memberEntity = memberMapper.mapToJpaEntity(findMember);
        ZoneMemberEntity entity = ZoneMemberEntity.builder()
                .status(ZoneMemberStatus.RESTING)
                .memberEntity(memberEntity)
                .mogakZoneEntity(mogakZoneEntity)
                .build();

        zoneMemberRepository.save(entity);
        return new JoinResponse(mogakZone.getZoneId().value());
    }

    @Override
    public int getZoneMemberCount(Long mogakZoneId) {
        return zoneMemberRepository.findAllByMogakZoneId(mogakZoneId).size();
    }
}
