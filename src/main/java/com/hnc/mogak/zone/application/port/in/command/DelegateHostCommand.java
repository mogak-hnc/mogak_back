package com.hnc.mogak.zone.application.port.in.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DelegateHostCommand {

    private Long mogakZoneId;
    private Long currentHostId;
    private Long newHostId;

}
