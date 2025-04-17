package com.hnc.mogak.challenge.domain.vo;

import java.time.LocalDate;

public record ChallengeDuration(
        LocalDate startDate,
        LocalDate endDate
) {
}
