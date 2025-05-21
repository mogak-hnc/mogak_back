package com.hnc.mogak.zone.adapter.out.persistence.repository;

import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneSummaryMemberImage;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneSummaryMemberImageRepository extends JpaRepository<ZoneSummaryMemberImage, Long> {

    @Query("SELECT zmi.imageUrl FROM ZoneSummaryMemberImage zmi WHERE zmi.mogakZoneId = :mogakZoneId")
    List<String> getMemberImagesByMogakZoneId(@Param(value = "mogakZoneId") Long mogakZoneId, Pageable pageable);

    void deleteByMogakZoneIdAndMemberId(Long mogakZoneId, Long memberId);

    @Query(value = """
        SELECT t.mogak_zone_id, t.image_url
        FROM (
            SELECT zsm.mogak_zone_id, zsm.image_url,
                   ROW_NUMBER() OVER (PARTITION BY zsm.mogak_zone_id ORDER BY zsm.id) AS rn
            FROM zone_summary_member_image zsm
            WHERE zsm.mogak_zone_id IN (:zoneIds)
        ) t
        WHERE t.rn <= :size;
    """, nativeQuery = true)
    List<Object[]> findTopImagesByZoneIds(@Param("zoneIds") List<Long> zoneIds, @Param("size") int size);

    void deleteByMogakZoneId(Long mogakZoneId);
}
