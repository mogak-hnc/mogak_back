package com.hnc.mogak.zone.adapter.out.persistence;

import com.hnc.mogak.zone.adapter.in.web.dto.ChatMessageResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ChatEntity;
import com.hnc.mogak.zone.adapter.out.persistence.repository.ChatRepository;
import com.hnc.mogak.zone.application.port.out.ChatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class ChatAdapter implements ChatPort {

    private final ChatRepository chatRepository;

    @Override
    public void save(Long memberId, Long mogakZoneId, String nickname, String imageUrl, String message, LocalDateTime localDate) {
        chatRepository.save(ChatEntity.builder()
                .memberId(memberId)
                .mogakZoneId(mogakZoneId)
                .nickname(nickname)
                .imageUrl(imageUrl)
                .message(message)
                .saveTime(localDate)
                .build());
    }

    @Override
    public Page<ChatMessageResponse> loadMessagesByMogakZoneId(Long mogakZoneId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatEntity> chatEntities = chatRepository.findByMogakZoneIdOrderBySaveTimeAsc(mogakZoneId, pageable);

        return chatEntities.map(chat -> ChatMessageResponse.builder()
                .memberId(chat.getMemberId())
                .nickname(chat.getNickname())
                .imageUrl(chat.getImageUrl())
                .message(chat.getMessage())
                .now(formatLocalTime(chat.getSaveTime()))
                .build());
    }

    private String formatLocalTime(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

}