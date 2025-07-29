package com.team4real.demo.domain.matching.dto;

import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import lombok.Builder;

@Builder
public record BrandUnitDto(
        Long matchingId,
        Long brandId,
        String name,
        String image,
        int matchScore,
        String keywords,
        MatchingStatus status,
        boolean liked
) {
    public static BrandUnitDto from(Matching m, Brand brand, boolean liked) {
        return BrandUnitDto.builder()
                .matchingId(m.getMatchingId())
                .brandId(brand.getBrandId())
                .name(brand.getName())
                .image(brand.getImage())
                .matchScore(m.getMatchScore())
                .keywords(brand.getKeyword())
                .status(m.getStatus())
                .liked(liked)
                .build();
    }
}
