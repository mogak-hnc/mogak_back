package com.hnc.mogak.zone.adapter.out.persistence.repository;

import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ZoneMemberRepository extends JpaRepository<ZoneMemberEntity, Long> {

    @Query("SELECT zm FROM ZoneMemberEntity zm JOIN FETCH zm.memberEntity WHERE zm.mogakZoneEntity.id = :mogakZoneId")
    List<ZoneMemberEntity> findAllZoneMembersWithMembersByMogakZoneId(@Param("mogakZoneId") Long mogakZoneId);

    @Query("SELECT COUNT(zm) FROM ZoneMemberEntity zm WHERE zm.mogakZoneEntity.id = :mogakZoneId")
    int countByMogakZoneId(@Param("mogakZoneId") Long mogakZoneId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ZoneMemberEntity zm WHERE zm.mogakZoneEntity.id = :mogakZoneId AND zm.memberEntity.id = :memberId")
    void deleteMemberByMogakZoneId(@Param("mogakZoneId") Long mogakZoneId, @Param("memberId") Long memberId);

}
