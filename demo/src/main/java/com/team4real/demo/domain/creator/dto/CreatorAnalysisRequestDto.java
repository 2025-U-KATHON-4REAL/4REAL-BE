package com.team4real.demo.domain.creator.dto;

import java.util.List;

public record CreatorAnalysisRequestDto(
    String name,
    String gender,
    int age,
    String profileImageUrl,

    List<String> interestCategories,
    List<String> brandThemes,
    String skinType,
    String skinTone,
    List<String> makeupStyle,

    String sns,
    String targetGender,
    String targetAgeGroup,
    List<String> contentTypes,
    List<String> contentTopics,
    String contentLength,
    List<String> brandPreferences
) {}