package com.hnc.mogak.worry.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateWorryRequest {

    private String title;
    private String body;

}
