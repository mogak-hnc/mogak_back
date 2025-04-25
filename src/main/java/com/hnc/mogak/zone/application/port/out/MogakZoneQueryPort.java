package com.hnc.mogak.zone.application.port.out;

import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;

import java.util.List;

public interface MogakZoneQueryPort {

    List<String> getTags(Long mogakZoneId);

    MogakZone findById(Long mogakZoneId);

    ZoneOwner findByMogakZoneId(Long mogakZoneId);

}
