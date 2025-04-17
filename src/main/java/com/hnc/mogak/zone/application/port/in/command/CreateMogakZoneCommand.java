package com.hnc.mogak.zone.application.port.in.command;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Getter
public class CreateMogakZoneCommand {

    private final String name;
    private final int maxCapacity;
    private final String imageUrl;
    private final String password;
    private final boolean chatEnabled;
    private boolean loginRequired;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Long memberId;
    private final Set<String> tagNames;

}