package com.hnc.mogak.member.domain.vo;

import java.util.UUID;

public record PlatformInfo(
        String provider,
        String providerId
) {
    public PlatformInfo deleteMember() {
        return new PlatformInfo(provider, UUID.randomUUID().toString());
    }
}
