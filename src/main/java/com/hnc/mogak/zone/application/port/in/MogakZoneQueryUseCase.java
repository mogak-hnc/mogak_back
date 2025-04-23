package com.hnc.mogak.zone.application.port.in;

import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneDetailResponse;
import com.hnc.mogak.zone.application.port.in.query.GetMogakZoneDetailQuery;

public interface MogakZoneQueryUseCase {
    MogakZoneDetailResponse getDetail(GetMogakZoneDetailQuery detailQuery);

}
