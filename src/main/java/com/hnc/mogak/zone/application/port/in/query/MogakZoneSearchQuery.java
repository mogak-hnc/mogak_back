package com.hnc.mogak.zone.application.port.in.query;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MogakZoneSearchQuery {

    private String tag;
    private Sort sort;
    private int page;
    private int size;

    public static enum Sort {
        participant, recent
    }

}