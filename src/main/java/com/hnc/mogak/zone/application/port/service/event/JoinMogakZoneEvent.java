package com.hnc.mogak.zone.application.port.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class JoinMogakZoneEvent extends ApplicationEvent {

    private Long mogakZoneId;

    public JoinMogakZoneEvent(Object source, Long mogakZoneId) {
        super(source);
        this.mogakZoneId = mogakZoneId;
    }

}