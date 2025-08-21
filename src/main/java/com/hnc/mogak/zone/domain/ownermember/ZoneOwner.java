package com.hnc.mogak.zone.domain.ownermember;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.domain.ownermember.vo.ZoneOwnerId;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoneOwner {

    private ZoneOwnerId zoneOwnerId;
    private Member hostMember;
    private MogakZone mogakZone;

    public static ZoneOwner withId(Long id, Member member, MogakZone mogakZone) {
        ZoneOwnerId zoneOwnerId = new ZoneOwnerId(id);
        return new ZoneOwner(zoneOwnerId, member, mogakZone);
    }

}
