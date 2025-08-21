package com.hnc.mogak.zone.application.port.out;

import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneSearchResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.TagNameResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneSummary;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface MogakZoneQueryPort {

    List<String> getTags(Long mogakZoneId);

    MogakZone findById(Long mogakZoneId);

    ZoneOwner findZoneOwnerByMogakZoneId(Long mogakZoneId);

    Page<MogakZoneSearchResponse> searchMogakZone(MogakZoneSearchQuery mogakZoneSearchQuery, Pageable pageable);

    ZoneSummary getSummaryDetail(Long mogakZoneId);

    List<ZoneSummary> findTopZoneSummariesByJoinCount(int size);

    List<TagNameResponse> getPopularTags();

    void saveZoneSummaryMemberImage(Long mogakZoneId, Long memberId, String memberImageUrl);

    Map<Long, List<String>> getZoneMemberImagesByZoneIds(List<Long> zoneIds, int size);

    Long findZoneOwnerIdByMogakZoneId(Long mogakZoneId);

    MogakZone findWithLock(Long mogakZoneId);

    ZoneSummary findSummaryWithLock(Long value);

}
