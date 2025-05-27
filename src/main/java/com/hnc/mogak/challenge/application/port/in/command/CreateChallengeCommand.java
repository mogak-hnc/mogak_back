package com.hnc.mogak.challenge.application.port.in.command;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateChallengeCommand {

    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long memberId;
    private boolean official;
    private Long badgeId;

}