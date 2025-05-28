package com.hnc.mogak.challenge.application.port.in.query;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChallengeSearchQuery {

    private String search;
    private Boolean official;
    private Sort sort;
    private ChallengeStatus status;
    private int page;
    private int size;


    public static enum Sort {
        participant, recent
    }

}
