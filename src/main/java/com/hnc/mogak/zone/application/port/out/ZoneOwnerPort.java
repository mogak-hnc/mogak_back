package com.hnc.mogak.zone.application.port.out;

import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;

public interface ZoneOwnerPort {

    void saveZoneOwner(Member member, MogakZone mogakZone);

    ZoneOwner findByMogakZoneId(Long mogakZoneId);

}
