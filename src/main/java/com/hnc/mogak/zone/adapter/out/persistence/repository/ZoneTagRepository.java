package com.hnc.mogak.zone.adapter.out.persistence.repository;

import com.hnc.mogak.zone.adapter.in.web.dto.TagNameResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.TagEntity;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneTagEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneTagRepository extends JpaRepository<ZoneTagEntity, Long> {

    @Query("SELECT zt FROM ZoneTagEntity zt WHERE zt.zone.id = :zoneId")
    List<ZoneTagEntity> findAllByZoneId(@Param("zoneId") Long zoneId);

    @Query("SELECT zt.tag.name FROM ZoneTagEntity zt WHERE zt.zone.id = :mogakZoneId")
    List<String> findTagNamesByMogakZoneId(@Param("mogakZoneId") Long mogakZoneId);

    @Query("SELECT zt.tag.name " +
            "FROM ZoneTagEntity zt " +
            "GROUP BY zt.tag.name " +
            "ORDER BY COUNT(zt.tag.name) DESC")
    List<String> getPopularTagNames(Pageable pageable);

}
