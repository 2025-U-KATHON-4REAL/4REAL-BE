package com.team4real.demo.domain.chat.entity;

import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private AuthUser sender; // SYSTEM 메시지일 경우 null 허용

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Builder
    public ChatMessage(ChatRoom chatRoom, AuthUser sender, String content, MessageType type) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.content = content;
        this.type = type;
    }

    public static ChatMessage system(ChatRoom chatRoom, String content) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .content(content)
                .type(MessageType.SYSTEM)
                .sender(null)
                .build();
    }
}