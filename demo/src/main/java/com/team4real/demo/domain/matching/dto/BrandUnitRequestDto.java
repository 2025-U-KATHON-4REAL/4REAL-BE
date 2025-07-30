package com.team4real.demo.domain.matching.dto;

import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import lombok.Builder;

@Builder
public record BrandUnitRequestDto(
        Long matchingId,
        Long brandId,
        String name,
        String image,
        int matchScore,
        String keywords,
        String description,
        MatchingStatus status,
        boolean liked,
        String proposal
) {
    public static BrandUnitRequestDto from(Matching m, Brand brand, boolean liked) {
        return BrandUnitRequestDto.builder()
                .matchingId(m.getMatchingId())
                .brandId(brand.getBrandId())
                .name(brand.getName())
                .image(brand.getImage())
                .matchScore(m.getMatchScore())
                .keywords(brand.getKeyword())
                .description(brand.getDescription())
                .status(m.getStatus())
                .liked(liked)
                .proposal(m.getProposal())
                .build();
    }
}
