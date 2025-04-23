package com.hnc.mogak.zone.domain.zone.vo;

import java.time.LocalDate;

public record ZoneDuration(
        LocalDate startDate,
        LocalDate endDate
) {
}
