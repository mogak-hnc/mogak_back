package com.hnc.mogak.zone.adapter.out.persistence.repository;

import com.hnc.mogak.zone.adapter.out.persistence.entity.MogakZoneEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MogakZoneRepository extends JpaRepository<MogakZoneEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM MogakZoneEntity m WHERE m.id = :mogakZoneId")
    Optional<MogakZoneEntity> findByIdWithLock(@Param(value = "mogakZoneId") Long mogakZoneId);

}