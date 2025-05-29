package com.hnc.mogak.worry.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateAiCommentEvent {

    private String title;
    private String body;
    private Integer worryId;

}
