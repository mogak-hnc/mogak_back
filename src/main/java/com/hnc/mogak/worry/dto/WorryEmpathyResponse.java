package com.hnc.mogak.worry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorryEmpathyResponse {

    private Integer worryId;
    private Integer empathyCount;
    private Boolean hasEmpathized;

}
