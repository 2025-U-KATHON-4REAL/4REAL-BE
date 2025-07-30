package com.team4real.demo.domain.chat.controller;

import com.team4real.demo.domain.chat.dto.ChatMessageContentDto;
import com.team4real.demo.domain.chat.dto.ChatMessageResponseDto;
import com.team4real.demo.domain.chat.dto.ChatRoomPreviewDto;
import com.team4real.demo.domain.chat.service.ChatRoomService;
import com.team4real.demo.domain.chat.service.ChatService;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chat")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    @Operation(summary = "요청한 제안의 채팅 목록 조회")
    @GetMapping("/sent")
    public ResponseEntity<List<ChatRoomPreviewDto>> getSentChats(
            @RequestParam(required = false) MatchingStatus status // null이면 전체 조회
    ) {
        return ResponseEntity.ok(chatRoomService.getMyChatRooms(status, true));
    }

    @Operation(summary = "받은 제안의 채팅 목록 조회")
    @GetMapping("/received")
    public ResponseEntity<List<ChatRoomPreviewDto>> getReceivedChats(
            @RequestParam(required = false) MatchingStatus status
    ) {
        return ResponseEntity.ok(chatRoomService.getMyChatRooms(status, false));
    }

    @Operation(summary = "특정 채팅방의 메시지 목록 조회")
    @GetMapping("/{chatRoomId}/message")
    public ResponseEntity<List<ChatMessageResponseDto>> getAllMessages(@PathVariable final Long chatRoomId) {
        return ResponseEntity.ok(chatService.getAllMessages(chatRoomId));
    }

    @Operation(summary = "특정 채팅방에 메시지 전송")
    @PostMapping("/{chatRoomId}/messages")
    public ResponseEntity<Void> sendMessage(@PathVariable final Long chatRoomId, @RequestBody @Valid ChatMessageContentDto requestDto) {
        chatService.sendUserMessage(chatRoomId, requestDto);
        return ResponseEntity.ok().build();
    }
}