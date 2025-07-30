package com.team4real.demo.domain.matching.entity;

import com.team4real.demo.domain.auth.entity.Role;
import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.global.common.entity.BaseTimeEntity;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Matching extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long matchingId;

    // 크리에이터
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Creator creator;

    // 브랜드
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role initiator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchingStatus status;  // RECOMMENDED, PENDING, ACCEPTED, REJECTED

    @Column(nullable = false)
    private int matchScore; // 매칭률

    private String proposal; // 제안 메시지
    private String reply; // 수락 사유/거절 사유

    @Builder
    public Matching(Creator creator, Brand brand, MatchingStatus status, int matchScore, Role initiator) {
        this.creator = creator;
        this.brand = brand;
        this.status = status;
        this.matchScore = matchScore;
        if (initiator == Role.ADMIN) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        this.initiator = initiator;
    }

    public void updateMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public void pend(String proposal) {
        if (this.status != MatchingStatus.RECOMMENDED) {
            throw new IllegalStateException("추천 상태에서만 대기로 변경할 수 있습니다.");
        }
        this.status = MatchingStatus.PENDING;
        this.proposal = proposal;
    }

    public void accept(String reply) {
        if (this.status != MatchingStatus.PENDING) {
            throw new IllegalStateException("요청 상태에서만 수락할 수 있습니다.");
        }
        this.status = MatchingStatus.ACCEPTED;
        this.reply = reply;
    }

    public void reject(String reply) {
        if (this.status != MatchingStatus.PENDING) {
            throw new IllegalStateException("요청 상태에서만 거절할 수 있습니다.");
        }
        this.status = MatchingStatus.REJECTED;
        this.reply = reply;
    }
}
