package com.team4real.demo.domain.matching.dto;

import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import lombok.Builder;

@Builder
public record MatchingBrandsListResponseDto(
        Long matchingId,
        Long brandId,
        String name,
        String image,
        int matchScore,
        String keywords,
        MatchingStatus status
) {
    public static MatchingBrandsListResponseDto from(Matching m, Brand brand) {
        return MatchingBrandsListResponseDto.builder()
                .matchingId(m.getMatchingId())
                .brandId(brand.getId())
                .name(brand.getName())
                .image(brand.getImage())
                .matchScore(m.getMatchScore())
                .keywords(brand.getKeyword())
                .status(m.getStatus())
                .build();
    }
}
