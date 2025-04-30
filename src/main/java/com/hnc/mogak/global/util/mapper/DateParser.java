package com.hnc.mogak.global.util.mapper;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class DateParser {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate[] parsePeriod(String period) {
        String[] dates = period.split("~");
        LocalDate startDate = LocalDate.parse(dates[0].trim(), FORMATTER);
        LocalDate endDate = LocalDate.parse(dates[1].trim(), FORMATTER);
        return new LocalDate[] { startDate, endDate };
    }

}