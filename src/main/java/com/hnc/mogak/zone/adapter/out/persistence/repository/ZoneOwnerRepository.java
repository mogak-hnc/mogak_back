package com.hnc.mogak.zone.adapter.out.persistence.repository;

import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneOwnerRepository extends JpaRepository<ZoneOwnerEntity, Long> {

    @Query("SELECT zo FROM ZoneOwnerEntity zo WHERE zo.mogakZoneEntity.id = :mogakZoneId")
    ZoneOwnerEntity findByMogakZoneId(@Param("mogakZoneId") Long mogakZoneId);
}
