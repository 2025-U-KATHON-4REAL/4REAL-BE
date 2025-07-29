package com.team4real.demo.domain.chat.controller;

import com.team4real.demo.domain.chat.dto.ChatMessageDto;
import com.team4real.demo.domain.chat.dto.ChatMessageSendRequestDto;
import com.team4real.demo.domain.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody @Valid ChatMessageSendRequestDto requestDto) {
        chatService.sendUserMessage(requestDto);
        return ResponseEntity.ok().build();
    }
}