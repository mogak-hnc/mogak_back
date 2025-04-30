package com.hnc.mogak.zone.application.port.in;

import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneDetailResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneMainResponse;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneDetailQuery;

import java.util.List;

public interface MogakZoneQueryUseCase {
    MogakZoneDetailResponse getDetail(MogakZoneDetailQuery detailQuery);
    List<MogakZoneMainResponse> getMainPage();
}
