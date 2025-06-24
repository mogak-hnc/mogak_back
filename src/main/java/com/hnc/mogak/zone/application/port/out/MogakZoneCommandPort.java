package com.hnc.mogak.zone.application.port.out;

import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.out.persistence.entity.TagEntity;
import com.hnc.mogak.zone.domain.zone.MogakZone;

import java.util.Set;

public interface MogakZoneCommandPort {

//    CreateMogakZoneResponse createMogakZone(MogakZone mogakZone, Set<TagEntity> tagSet);
    MogakZone createMogakZone(MogakZone mogakZone, Set<TagEntity> tagSet);

    void saveZoneOwner(Member member, MogakZone mogakZone);

    void saveMogakZone(MogakZone mogakZone);

    void saveZoneSummary(MogakZone mogakZone, Set<TagEntity> tagEntitySet);

    void deleteZoneSummaryMemberImage(Long mogakZoneId, Long memberId);

    void deleteMogakZone(MogakZone mogakZone);

    void updateHost(Long mogakZoneId, Member newOwnerMember);

    void updateMogakZone(MogakZone findMogakZone, String imageUrl);
}
