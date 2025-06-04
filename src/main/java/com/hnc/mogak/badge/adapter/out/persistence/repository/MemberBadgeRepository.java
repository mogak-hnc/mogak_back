package com.hnc.mogak.badge.adapter.out.persistence.repository;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeEntity;
import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import com.hnc.mogak.badge.adapter.out.persistence.entity.MemberBadgeEntity;
import com.hnc.mogak.badge.event.MemberBadgeCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberBadgeRepository extends JpaRepository<MemberBadgeEntity, Long> {

    @Query("SELECT COUNT(mb) > 0 FROM MemberBadgeEntity mb " +
            "WHERE mb.memberEntity.id = :memberId " +
            "AND mb.badgeEntity.badgeType = :badgeType")
    boolean existsByMemberIdAndBadgeType(
            @Param(value = "memberId") Long memberId,
            Long badgeId, @Param(value = "badgeType") BadgeType badgeType
    );

    @Query("SELECT mb.badgeEntity FROM MemberBadgeEntity mb WHERE mb.memberEntity.id = :memberId")
    List<BadgeEntity> findBadgesByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT CASE WHEN COUNT(mb) > 0 THEN true ELSE false END " +
            "FROM MemberBadgeEntity mb " +
            "WHERE mb.memberEntity.id = :memberId " +
            "AND mb.badgeEntity.id = :badgeId " +
            "AND mb.badgeEntity.badgeType = :badgeType")
    boolean existsByMemberIdAndBadgeIdAndBadgeType(@Param(value = "memberId") Long memberId,
                                                   @Param(value = "badgeId") Long badgeId,
                                                   @Param(value = "badgeType") BadgeType badgeType);

    @Query("SELECT mb.memberEntity.id AS memberId, COUNT(mb.id) AS badgeCount " +
            "FROM MemberBadgeEntity mb " +
            "WHERE mb.memberEntity.id IN :memberIds " +
            "GROUP BY mb.memberEntity.id")
    List<MemberBadgeCountProjection> findBadgeCountByMemberIds(@Param(value = "memberIds") List<Long> memberIds);

}
