package com.hnc.mogak.member.domain.vo;

public record MemberInfo(
        String nickname,
        String email,
        String password,
        String imagePath,
        String name
) {
}
