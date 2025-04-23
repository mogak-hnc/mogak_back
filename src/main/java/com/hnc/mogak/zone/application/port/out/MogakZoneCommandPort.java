package com.hnc.mogak.zone.application.port.out;

import com.hnc.mogak.zone.adapter.in.web.dto.CreateMogakZoneResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.TagEntity;
import com.hnc.mogak.zone.domain.zone.MogakZone;

import java.util.Set;

public interface MogakZoneCommandPort {

//    CreateMogakZoneResponse createMogakZone(MogakZone mogakZone, Set<TagEntity> tagSet);
    MogakZone createMogakZone(MogakZone mogakZone, Set<TagEntity> tagSet);

}
