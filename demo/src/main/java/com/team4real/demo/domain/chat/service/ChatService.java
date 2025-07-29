package com.team4real.demo.domain.chat.service;

import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.domain.auth.service.AuthUserService;
import com.team4real.demo.domain.chat.dto.ChatMessageDto;
import com.team4real.demo.domain.chat.dto.ChatMessageContentDto;
import com.team4real.demo.domain.chat.entity.ChatMessage;
import com.team4real.demo.domain.chat.entity.ChatRoom;
import com.team4real.demo.domain.chat.entity.MessageType;
import com.team4real.demo.domain.chat.repository.ChatRoomRepository;
import com.team4real.demo.domain.chat.repository.ChatMessageRepository;
import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.domain.matching.repository.MatchingRepository;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MatchingRepository matchingRepository;
    private final AuthUserService authUserService;

    public void sendUserMessage(Long chatRoomId, ChatMessageContentDto requestDto) {
        sendMessage(new ChatMessageDto(chatRoomId, requestDto.content(), MessageType.USER));
    }

    private void sendMessage(ChatMessageDto requestDto) {
        AuthUser sender = authUserService.getCurrentAuthUser();

        // 채팅방 조회 또는 생성
        ChatRoom chatRoom = chatRoomRepository.findByMatching_MatchingId(requestDto.matchingId())
                .orElseGet(() -> {
                    Matching matching = matchingRepository.findById(requestDto.matchingId())
                            .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
                    return chatRoomRepository.save(new ChatRoom(matching));
                });

        // 권한 검사 (SYSTEM 메시지도 sender가 유효한 경우만 허용)
        Matching m = chatRoom.getMatching();
        boolean isParticipant = m.getCreator().getAuthUser().equals(sender)
                || m.getBrand().getAuthUser().equals(sender);
        if (!isParticipant) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        // 메시지 생성 (타입 분기)
        ChatMessage message;
        switch (requestDto.type()) {
            case USER -> {
                message = ChatMessage.builder()
                        .chatRoom(chatRoom)
                        .sender(sender)
                        .content(requestDto.content())
                        .type(MessageType.USER)
                        .build();
            }
            case SYSTEM -> {
                message = ChatMessage.builder()
                        .chatRoom(chatRoom)
                        .sender(null)  // SYSTEM 메시지는 sender 없음
                        .content(requestDto.content())
                        .type(MessageType.SYSTEM)
                        .build();
            }
            case INFO -> {
                message = ChatMessage.builder()
                        .chatRoom(chatRoom)
                        .sender(sender)
                        .content(requestDto.content())
                        .type(MessageType.INFO)
                        .build();
            }
            default -> throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        chatMessageRepository.save(message);
        chatRoom.updateLastMessage(message.getContent(), LocalDateTime.now());
    }
}

