package com.hnc.mogak.challenge.domain.challenge.vo;

import java.time.LocalDate;

public record ChallengeDuration(
        LocalDate startDate,
        LocalDate endDate
) {
}
