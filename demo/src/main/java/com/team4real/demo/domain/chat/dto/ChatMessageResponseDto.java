package com.team4real.demo.domain.chat.dto;

import com.team4real.demo.domain.chat.entity.ChatMessage;
import com.team4real.demo.domain.chat.entity.MessageType;

import java.time.LocalDateTime;

public record ChatMessageResponseDto(
        Long messageId,
        String content,
        MessageType messageType,
        Long senderId,
        LocalDateTime createdAt
) {
    public static ChatMessageResponseDto from(ChatMessage chatMessage) {
        return new ChatMessageResponseDto(
                chatMessage.getId(),
                chatMessage.getContent(),
                chatMessage.getType(),
                chatMessage.getSender().getAuthUserId(),
                chatMessage.getCreatedAt()
        );
    }
}