package com.team4real.demo.domain.chat.dto;

public record ChatMessageSendRequestDto(
        Long matchingId,
        String content
) {}