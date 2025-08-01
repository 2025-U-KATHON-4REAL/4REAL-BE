package com.team4real.demo.domain.matching.dto;

import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import lombok.Builder;

@Builder
public record CreatorUnitDto(
        Long matchingId,
        Long creatorId,
        String name,
        String image,
        int matchScore,
        MatchingStatus status,
        boolean liked
) {
    public static CreatorUnitDto from(Matching m, Creator creator, boolean liked) {
        return CreatorUnitDto.builder()
                .matchingId(m.getMatchingId())
                .creatorId(creator.getCreatorId())
                .name(creator.getNickname())
                .image(creator.getImage())
                .matchScore(m.getMatchScore())
                .status(m.getStatus())
                .liked(liked)
                .build();
    }
}
