package com.hnc.mogak.zone.application.port.out;

import com.hnc.mogak.zone.adapter.in.web.dto.ChatMessageResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatPort {

    void save(Long memberId,
              Long mogakZoneId,
              String nickname,
              String imageUrl,
              String message,
              LocalDateTime localDate
    );

    List<ChatMessageResponse> loadMessagesByMogakZoneId(Long mogakZoneId);

}