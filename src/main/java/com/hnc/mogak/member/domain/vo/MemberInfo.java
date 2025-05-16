package com.hnc.mogak.member.domain.vo;

import java.util.UUID;

public record MemberInfo(
        String nickname,
        String email,
        String password,
        String imagePath,
        String name,
        boolean withdrawn,
        boolean showBadge
) {

    public MemberInfo deleteMember() {
        return new MemberInfo(UUID.randomUUID().toString(), email, password, "Default", name, true, false);
    }

    public MemberInfo updateMemberInfo(String imageUrl, String nickname, boolean showBadge) {
        return new MemberInfo(nickname, email, password, imageUrl, name, withdrawn, showBadge);
    }

}
