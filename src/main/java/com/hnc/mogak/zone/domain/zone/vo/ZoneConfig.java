package com.hnc.mogak.zone.domain.zone.vo;

public record ZoneConfig(
                         boolean chatEnabled,
                         boolean passwordEnabled,
                         int maxCapacity) {
}
