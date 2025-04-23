package com.hnc.mogak.zone.domain.ownermember;

import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.zone.domain.ownermember.vo.ZoneOwnerId;
import com.hnc.mogak.zone.domain.zone.MogakZone;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ZoneOwner {

    private ZoneOwnerId zoneOwnerId;
    private Member hostMember;
    private MogakZone mogakZone;

    public static ZoneOwner withId(Long id, Member member, MogakZone mogakZone) {
        ZoneOwnerId zoneOwnerId = new ZoneOwnerId(id);
        return new ZoneOwner(zoneOwnerId, member, mogakZone);
    }

}
