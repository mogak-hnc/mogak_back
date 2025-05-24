package com.hnc.mogak.worry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WorryCommentDeleteResponse {

    private Integer worryId;
    private Integer commentId;

}
