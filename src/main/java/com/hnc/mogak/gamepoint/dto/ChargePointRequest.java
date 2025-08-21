package com.hnc.mogak.gamepoint.dto;

import lombok.Getter;

@Getter
public class ChargePointRequest {

    private Long memberId;
    private Integer amount;
    private String orderNo;

}