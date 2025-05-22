package com.hnc.mogak.worry.dto;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum WorryDuration {
    ONE_MINUTE(Duration.ofMinutes(1)),
    ONE_HOUR(Duration.ofHours(1)),
    THREE_HOURS(Duration.ofHours(3)),
    SIX_HOURS(Duration.ofHours(6)),
    TWELVE_HOURS(Duration.ofHours(12)),
    TWENTY_FOUR_HOURS(Duration.ofHours(24));

    private final Duration duration;

    WorryDuration(Duration duration) {
        this.duration = duration;
    }

}