package com.hnc.mogak.zone.adapter.out.persistence.repository;

import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneSearchResponse;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MogakZoneQueryDsl {

    Page<MogakZoneSearchResponse> findMogakZone(MogakZoneSearchQuery query, Pageable pageable);

}
