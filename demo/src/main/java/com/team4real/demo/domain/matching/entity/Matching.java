package com.team4real.demo.domain.matching.entity;

import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.global.common.entity.BaseTimeEntity;
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

    // 요청한 크리에이터
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Creator creator;

    // 제안받은 브랜드
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchingStatus status;  // PENDING, ACCEPTED, REJECTED

    @Column(nullable = false)
    private int matchScore; // 매칭률 (%)

    private String proposalMessage; // 선택: 제안 메시지 등
    private String rejectionReason; // 선택: 거절 사유 등

    @Builder
    public Matching(Creator creator, Brand brand, MatchingStatus status, int matchScore) {
        this.creator = creator;
        this.brand = brand;
        this.status = status;
        this.matchScore = matchScore;
    }

    public void updateMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }
    public void updateProposalMessage(String proposalMessage) {
        this.proposalMessage = proposalMessage;
    }
    public void updateRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void pend() {
        if (this.status != MatchingStatus.PENDING) {
            throw new IllegalStateException("요청 상태에서만 보류로 변경할 수 있습니다.");
        }
        this.status = MatchingStatus.PENDING;
    }

    public void accept() {
        if (this.status != MatchingStatus.PENDING) {
            throw new IllegalStateException("요청 상태에서만 수락할 수 있습니다.");
        }
        this.status = MatchingStatus.ACCEPTED;
    }

    public void reject() {
        if (this.status != MatchingStatus.PENDING) {
            throw new IllegalStateException("요청 상태에서만 거절할 수 있습니다.");
        }
        this.status = MatchingStatus.REJECTED;
    }
}
