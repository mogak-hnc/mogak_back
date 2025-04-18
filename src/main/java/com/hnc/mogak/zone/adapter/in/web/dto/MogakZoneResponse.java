package com.hnc.mogak.zone.adapter.in.web.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MogakZoneResponse {

    private Long mogakZoneId;
    private String name;

    private int maxCapacity;

    private String imageUrl;

    private String password;

    private boolean chatEnabled;
    private boolean loginRequired;

    private LocalDate startDate;
    private LocalDate endDate;

    private Set<String> tagNames;

}
