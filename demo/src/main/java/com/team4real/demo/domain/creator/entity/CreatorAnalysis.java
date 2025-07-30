package com.team4real.demo.domain.creator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CreatorAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long creatorAnalysisId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Creator creator;

    @ElementCollection private List<String> interestCategories;
    @ElementCollection private List<String> brandThemes;
    private String skinType;
    private String skinTone;
    @ElementCollection private List<String> makeupStyle;

    private String sns;
    private String targetGender;
    private String targetAgeGroup;
    @ElementCollection private List<String> contentTypes;
    @ElementCollection private List<String> contentTopics;
    private String contentLength;
    @ElementCollection private List<String> brandPreferences;
}
