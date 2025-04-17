package com.hnc.mogak.challenge.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateChallengeRequest {

    @NotNull
    private String title;

    private String description;
    private String period;

}
