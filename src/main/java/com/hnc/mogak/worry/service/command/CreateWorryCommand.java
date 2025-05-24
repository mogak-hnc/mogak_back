package com.hnc.mogak.worry.service.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWorryCommand {

    private Long memberId;
    private String title;
    private String body;
    private Duration duration;
    private LocalDateTime createdAt;

}