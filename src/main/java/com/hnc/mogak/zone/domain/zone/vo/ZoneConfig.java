package com.hnc.mogak.zone.domain.zone.vo;

public record ZoneConfig(boolean loginRequired,
                         boolean chatEnabled,
                         int maxCapacity) {
}
