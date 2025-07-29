package com.team4real.demo.domain.chat.controller;

import com.team4real.demo.domain.chat.dto.ChatMessageContentDto;
import com.team4real.demo.domain.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/{chatRoomId}/messages")
    public ResponseEntity<Void> sendMessage(@PathVariable final Long chatRoomId, @RequestBody @Valid ChatMessageContentDto requestDto) {
        chatService.sendUserMessage(chatRoomId, requestDto);
        return ResponseEntity.ok().build();
    }
}