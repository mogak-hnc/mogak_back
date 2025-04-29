package com.hnc.mogak.zone.application.port.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OutMogakZoneEvent extends ApplicationEvent {

    private Long mogakZoneId;

    public OutMogakZoneEvent(Object source, Long mogakZoneId) {
        super(source);
        this.mogakZoneId = mogakZoneId;
    }
}
