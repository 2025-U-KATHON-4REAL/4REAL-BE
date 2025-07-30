package com.team4real.demo.domain.chat.entity;

import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matching_id", nullable = false, unique = true)
    private Matching matching;

    private String lastMessage;
    private LocalDateTime lastMessageAt;

    @Builder
    public ChatRoom(Matching matching) {
        this.matching = matching;
        this.lastMessage = null;
        this.lastMessageAt = null;
    }

    public void updateLastMessage(String content, LocalDateTime time) {
        this.lastMessage = content;
        this.lastMessageAt = time;
    }
}
