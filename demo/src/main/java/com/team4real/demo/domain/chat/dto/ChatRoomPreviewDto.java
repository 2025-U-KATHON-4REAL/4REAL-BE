package com.team4real.demo.domain.chat.dto;

import com.team4real.demo.domain.chat.entity.ChatRoom;
import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.domain.matching.entity.MatchingStatus;

import java.time.LocalDateTime;

public record ChatRoomPreviewDto(
        Long chatRoomId,
        String brandName,
        String image,
        String lastMessage,
        LocalDateTime lastMessageAt,
        MatchingStatus matchingStatus
) {
    public static ChatRoomPreviewDto from(ChatRoom chatRoom) {
        Matching matching = chatRoom.getMatching();
        return new ChatRoomPreviewDto(
                chatRoom.getChatRoomId(),
                matching.getBrand().getName(),
                matching.getBrand().getImage(),
                chatRoom.getLastMessage(),
                chatRoom.getLastMessageAt(),
                matching.getStatus()
        );
    }
}
