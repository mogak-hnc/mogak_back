package com.hnc.mogak.member.domain.vo;

import java.util.UUID;

public record MemberInfo(
        String nickname,
        String email,
        String password,
        String imagePath,
        String name,
        boolean withdrawn
) {

    public MemberInfo deleteMember() {
        return new MemberInfo(UUID.randomUUID().toString(), email, password, "Default", name, true);
    }

    public MemberInfo updateImage(String s3ImageUrl) {
        return new MemberInfo(nickname, email, password, s3ImageUrl, name, withdrawn);
    }

    public MemberInfo updateNickname(String nickname) {
        return new MemberInfo(nickname, email, password, imagePath, name, withdrawn);
    }

}
