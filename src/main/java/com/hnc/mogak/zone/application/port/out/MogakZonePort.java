package com.hnc.mogak.zone.application.port.out;

import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.TagEntity;
import com.hnc.mogak.zone.domain.MogakZone;

import java.util.List;
import java.util.Set;

public interface MogakZonePort {

    MogakZoneResponse createMogakZone(MogakZone mogakZone, Set<TagEntity> tagSet);
}
