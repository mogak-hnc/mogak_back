package com.hnc.mogak.challenge.application.port.in.query;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChallengeSearchQuery {

    private String search;
    private boolean official;
    private Sort sort;
    private int page;
    private int size;


    public static enum Sort {
        participant, recent
    }

}
