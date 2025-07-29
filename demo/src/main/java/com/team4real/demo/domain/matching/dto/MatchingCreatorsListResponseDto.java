package com.team4real.demo.domain.matching.dto;

import com.team4real.demo.domain.brand.entity.Brand;
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
        MatchingStatus status
) {
    public static MatchingCreatorsListResponseDto from(Matching m, Creator creator) {
        return MatchingCreatorsListResponseDto.builder()
                .matchingId(m.getMatchingId())
                .creatorId(creator.getId())
                .name(creator.getName())
                .image(creator.getImage())
                .matchScore(m.getMatchScore())
                .status(m.getStatus())
                .build();
    }
}
