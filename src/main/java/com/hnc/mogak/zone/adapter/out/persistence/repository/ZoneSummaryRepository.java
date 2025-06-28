package com.hnc.mogak.zone.adapter.out.persistence.repository;

import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneSummary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneSummaryRepository extends JpaRepository<ZoneSummary, Long> {

    ZoneSummary findByMogakZoneId(Long mogakZoneId);

    List<ZoneSummary> findAllByOrderByParticipantNumDesc(Pageable pageable);

    void deleteByMogakZoneId(Long id);

}
