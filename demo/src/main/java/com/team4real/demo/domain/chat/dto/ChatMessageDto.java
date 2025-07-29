package com.team4real.demo.domain.chat.dto;

import com.team4real.demo.domain.chat.entity.MessageType;

public record ChatMessageDto(
        Long matchingId,
        String content,
        MessageType type
) {}