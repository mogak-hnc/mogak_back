package com.hnc.mogak.zone.adapter.out.persistence.repository;

import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneSummary;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZoneSummaryRepository extends JpaRepository<ZoneSummary, Long> {

    ZoneSummary findByMogakZoneId(Long mogakZoneId);

    List<ZoneSummary> findAllByOrderByParticipantNumDesc(Pageable pageable);

    void deleteByMogakZoneId(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT zs FROM ZoneSummary zs WHERE zs.id = :id")
    Optional<ZoneSummary> findByIdWithLock(@Param(value = "id") Long id);

}
