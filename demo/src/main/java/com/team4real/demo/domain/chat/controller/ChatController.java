package com.team4real.demo.domain.chat.controller;

import com.team4real.demo.domain.chat.dto.ChatMessageContentDto;
import com.team4real.demo.domain.chat.dto.ChatRoomPreviewDto;
import com.team4real.demo.domain.chat.service.ChatRoomService;
import com.team4real.demo.domain.chat.service.ChatService;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    @PostMapping("/{chatRoomId}/messages")
    public ResponseEntity<Void> sendMessage(@PathVariable final Long chatRoomId, @RequestBody @Valid ChatMessageContentDto requestDto) {
        chatService.sendUserMessage(chatRoomId, requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sent")
    public ResponseEntity<List<ChatRoomPreviewDto>> getSentChats(
            @RequestParam(required = false) MatchingStatus status // null이면 전체 조회
    ) {
        return ResponseEntity.ok(chatRoomService.getMyChatRooms(status, true));
    }

    @GetMapping("/received")
    public ResponseEntity<List<ChatRoomPreviewDto>> getReceivedChats(
            @RequestParam(required = false) MatchingStatus status
    ) {
        return ResponseEntity.ok(chatRoomService.getMyChatRooms(status, false));
    }
}