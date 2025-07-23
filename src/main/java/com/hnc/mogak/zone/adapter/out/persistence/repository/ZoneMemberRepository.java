package com.hnc.mogak.zone.adapter.out.persistence.repository;

import com.hnc.mogak.challenge.adapter.out.persistence.projection.ChallengeInfoProjection;
import com.hnc.mogak.zone.adapter.out.persistence.ZoneInfoProjection;
import com.hnc.mogak.zone.adapter.out.persistence.entity.MogakZoneEntity;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneMemberEntity;
import com.hnc.mogak.zone.domain.zonemember.vo.ZoneMemberStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneMemberRepository extends JpaRepository<ZoneMemberEntity, Long> {

    @Query("SELECT zm FROM ZoneMemberEntity zm " +
            "JOIN FETCH zm.memberEntity " +
            "JOIN FETCH zm.mogakZoneEntity " +
            "WHERE zm.mogakZoneEntity.id = :mogakZoneId")
    List<ZoneMemberEntity> findAllZoneMembersWithMembersByMogakZoneId(@Param("mogakZoneId") Long mogakZoneId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT COUNT(zm) FROM ZoneMemberEntity zm WHERE zm.mogakZoneEntity.id = :mogakZoneId")
    int countByMogakZoneId(@Param("mogakZoneId") Long mogakZoneId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ZoneMemberEntity zm WHERE zm.mogakZoneEntity.id = :mogakZoneId AND zm.memberEntity.id = :memberId")
    void deleteMemberByMogakZoneId(@Param("mogakZoneId") Long mogakZoneId, @Param("memberId") Long memberId);

    @Modifying
    @Query("UPDATE ZoneMemberEntity zm SET zm.status = :status WHERE zm.mogakZoneEntity.id = :mogakZoneId AND zm.memberEntity.id = :memberId")
    void changeStatus(
            @Param("memberId") Long memberId,
            @Param("mogakZoneId") Long mogakZoneId,
            @Param("status") ZoneMemberStatus status
    );


    @Query("SELECT CASE WHEN COUNT(zm) > 0 THEN true ELSE false END " +
            "FROM ZoneMemberEntity zm " +
            "WHERE zm.mogakZoneEntity.id = :mogakZoneId AND zm.memberEntity.id = :memberId")
    boolean isMemberInMogakZone(@Param("mogakZoneId") Long mogakZoneId,
                                @Param("memberId") Long memberId);

    void deleteAllByMogakZoneEntity(MogakZoneEntity mogakZoneEntity);

    @Query("SELECT " +
            "zm.mogakZoneEntity.id AS mogakZoneId, " +
            "zm.mogakZoneEntity.name AS name " +
            "FROM ZoneMemberEntity zm " +
            "WHERE zm.memberEntity.id = :memberId")
    List<ZoneInfoProjection> findJoinedZones(Long memberId);

}