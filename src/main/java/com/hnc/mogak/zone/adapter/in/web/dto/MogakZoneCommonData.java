package com.hnc.mogak.zone.adapter.in.web.dto;

import com.hnc.mogak.zone.domain.ownermember.ZoneOwner;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import com.hnc.mogak.zone.domain.zonemember.ZoneMember;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MogakZoneCommonData {

    private List<String> tagNames;
    private MogakZone mogakZone;
    private ZoneOwner zoneOwner;
    private List<ZoneMember> zoneMemberList;
    private boolean passwordEnabled;

    public MogakZoneCommonData(List<String> tagNames, MogakZone mogakZone, ZoneOwner zoneOwner, List<ZoneMember> zoneMemberList, boolean passwordEnabled) {
        this.tagNames = tagNames;
        this.mogakZone = mogakZone;
        this.zoneOwner = zoneOwner;
        this.zoneMemberList = zoneMemberList;
        this.passwordEnabled = passwordEnabled;
    }

}