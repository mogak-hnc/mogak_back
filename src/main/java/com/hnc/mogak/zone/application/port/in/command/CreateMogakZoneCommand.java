package com.hnc.mogak.zone.application.port.in.command;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Getter
public class CreateMogakZoneCommand {

    private String name;
    private int maxCapacity;
    private String imageUrl;
    private String password;
    private boolean chatEnabled;
    private boolean loginRequired;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long memberId;
    private Set<String> tagNames;

}