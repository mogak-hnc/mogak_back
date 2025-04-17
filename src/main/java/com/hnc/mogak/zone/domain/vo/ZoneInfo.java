package com.hnc.mogak.zone.domain.vo;

public record ZoneInfo(
        String name,
        int maxCapacity,
        String imageUrl,
        String password
) {
}
