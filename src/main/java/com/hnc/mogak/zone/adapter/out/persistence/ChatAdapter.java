package com.hnc.mogak.zone.adapter.out.persistence;

import com.hnc.mogak.zone.adapter.in.web.dto.ChatMessageResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.ChatEntity;
import com.hnc.mogak.zone.adapter.out.persistence.repository.ChatRepository;
import com.hnc.mogak.zone.application.port.out.ChatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    public List<ChatMessageResponse> loadMessagesByMogakZoneId(Long mogakZoneId) {
        List<ChatEntity> chatEntityList = chatRepository.findAllByMogakZoneIdOrderBySaveTimeAsc(mogakZoneId);
        return chatEntityList.stream()
                .map(chat -> ChatMessageResponse.builder()
                        .memberId(chat.getMemberId())
                        .nickname(chat.getNickname())
                        .imageUrl(chat.getImageUrl())
                        .message(chat.getMessage())
                        .now(formatLocalTime(chat.getSaveTime()))
                        .build())
                .toList();
    }

    private String formatLocalTime(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

}