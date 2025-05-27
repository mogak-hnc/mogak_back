package com.hnc.mogak.badge.adapter.out.persistence.repository;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeEntity;
import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<BadgeEntity, Long> {

    Optional<BadgeEntity> findByBadgeType(BadgeType badgeType);

}
