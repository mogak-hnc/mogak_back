package com.hnc.mogak.badge.adapter.out.persistence.repository;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeEntity;
import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<BadgeEntity, Long> {

    Optional<BadgeEntity> findByBadgeType(BadgeType badgeType);

    List<BadgeEntity> findAllByBadgeType(BadgeType badgeType);

    @Query("SELECT b FROM BadgeEntity b WHERE b.badgeType = :badgeType AND b.conditionValue <= :conditionValue ORDER BY b.conditionValue DESC")
    List<BadgeEntity> findTopDurationBadge(@Param(value = "badgeType") BadgeType badgeType,
                                               @Param(value = "conditionValue") int conditionValue,
                                               Pageable pageable);

    Optional<BadgeEntity> findByBadgeTypeAndConditionValue(BadgeType badgeType, int conditionValue);

}
