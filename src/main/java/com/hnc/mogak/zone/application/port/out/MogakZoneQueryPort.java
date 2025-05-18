package com.hnc.mogak.zone.application.port.out;

import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneSearchResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.TagNameResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ZoneSummary;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MogakZoneQueryPort {

    List<String> getTags(Long mogakZoneId);

    MogakZone findById(Long mogakZoneId);

    ZoneOwner findByMogakZoneId(Long mogakZoneId);

    Page<MogakZoneSearchResponse> searchMogakZone(MogakZoneSearchQuery mogakZoneSearchQuery);

    ZoneSummary getSummaryDetail(Long mogakZoneId);

    List<ZoneSummary> findTopZoneSummariesByJoinCount(int size);

    List<TagNameResponse> getPopularTags();
}
