package com.hnc.mogak.zone.application.port.out;

import com.hnc.mogak.zone.adapter.in.web.dto.ChatMessageResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface ChatPort {

    void save(Long memberId,
              Long mogakZoneId,
              String nickname,
              String imageUrl,
              String message,
              LocalDateTime localDate
    );

    Page<ChatMessageResponse> loadMessagesByMogakZoneId(Long mogakZoneId, int page, int size);

}