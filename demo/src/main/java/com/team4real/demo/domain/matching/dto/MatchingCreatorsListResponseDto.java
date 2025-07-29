package com.team4real.demo.domain.matching.dto;

import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import lombok.Builder;

@Builder
public record MatchingCreatorsListResponseDto(
        Long matchingId,
        Long creatorId,
        String name,
        String image,
        int matchScore,
        MatchingStatus status,
        boolean liked
) {
    public static MatchingCreatorsListResponseDto from(Matching m, Creator creator, boolean liked) {
        return MatchingCreatorsListResponseDto.builder()
                .matchingId(m.getMatchingId())
                .creatorId(creator.getCreatorId())
                .name(creator.getName())
                .image(creator.getImage())
                .matchScore(m.getMatchScore())
                .status(m.getStatus())
                .liked(liked)
                .build();
    }
}
