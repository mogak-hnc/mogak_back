package com.hnc.mogak.zone.adapter.out.persistence.repository;

import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.adapter.out.persistence.entity.MogakZoneEntity;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneOwnerRepository extends JpaRepository<ZoneOwnerEntity, Long> {

    @Query("SELECT zo FROM ZoneOwnerEntity zo WHERE zo.mogakZoneEntity.id = :mogakZoneId")
    ZoneOwnerEntity findByMogakZoneId(@Param("mogakZoneId") Long mogakZoneId);

    @Query("SELECT zo.memberEntity.id FROM ZoneOwnerEntity zo WHERE zo.mogakZoneEntity.id = :mogakZoneId")
    Long findZoneOwnerIdByMogakZoneId(@Param(value = "mogakZoneId") Long mogakZoneId);

    void deleteByMogakZoneEntity(MogakZoneEntity mogakZoneEntity);

    @Modifying
    @Query("UPDATE ZoneOwnerEntity zo SET zo.memberEntity = :memberEntity WHERE zo.mogakZoneEntity.id = :mogakZoneId")
    void updateHost(@Param(value = "mogakZoneId") Long mogakZoneId, @Param(value = "memberEntity") MemberEntity memberEntity);

}