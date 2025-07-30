package com.team4real.demo.domain.creator.dto;

import java.util.List;

public record CreatorAnalysisResponseDto(
        String summary,
        List<String> beautyTraits,
        List<String> contentTraits,
        String customerAppeal
) {
    public static CreatorAnalysisResponseDto from(String summary, List<String> beautyTraits, List<String> contentTraits, String appeal) {
        return new CreatorAnalysisResponseDto(summary, beautyTraits, contentTraits, appeal);
    }
}