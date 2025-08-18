package com.hnc.mogak.gamepoint.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GamePointRepository extends JpaRepository<GamePoint, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT gp FROM GamePoint gp WHERE gp.member.id = :memberId")
    Optional<GamePoint> findByMemberIdWithLock(@Param(value = "memberId") Long memberId);

    Optional<GamePoint> findByMemberId(Long memberId);

}