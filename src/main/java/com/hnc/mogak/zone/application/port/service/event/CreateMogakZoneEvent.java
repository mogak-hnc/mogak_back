package com.hnc.mogak.zone.application.port.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

@Getter
public class CreateMogakZoneEvent extends ApplicationEvent {

    private Long mogakZoneId;

    public CreateMogakZoneEvent(Object source, Long mogakZoneId, String name, Set<String> tagNames) {
        super(source);
        this.mogakZoneId = mogakZoneId;
    }

}