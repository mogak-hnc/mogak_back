package com.hnc.mogak.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hnc.mogak.member.domain.vo.MemberId;
import com.hnc.mogak.member.domain.vo.MemberInfo;
import com.hnc.mogak.member.domain.vo.PlatformInfo;
import com.hnc.mogak.member.domain.vo.Role;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member {

    private MemberId memberId;
    private MemberInfo memberInfo;
    private PlatformInfo platformInfo;
    private Role role;

    public static Member withId(
            MemberId memberId,
            MemberInfo memberInfo,
            PlatformInfo platformInfo,
            Role role
    ) {
        return new Member(memberId, memberInfo, platformInfo, role);
    }

    public static Member withoutId(
            MemberInfo memberInfo,
            PlatformInfo platformInfo,
            Role role
    ) {
        return new Member(null, memberInfo, platformInfo, role);
    }

    public void assignId(MemberId memberId) {
        if (this.memberId == null) this.memberId = memberId;
    }

    public void assignId(Long memberId) {
        if (this.memberId == null) this.memberId = new MemberId(memberId);
    }

    public void deleteMember() {
        this.memberInfo = memberInfo.deleteMember();
        this.platformInfo = platformInfo.deleteMember();
    }

    public void  updateMemberInfo(String imageUrl, String nickname, boolean showBadge) {
        this.memberInfo = memberInfo.updateMemberInfo(imageUrl, nickname, showBadge);
    }

    public boolean isShowBadge() {
        return this.memberInfo.showBadge();
    }

}
