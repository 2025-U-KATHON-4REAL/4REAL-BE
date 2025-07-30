package com.team4real.demo.domain.creator.service;

import com.team4real.demo.domain.auth.service.AuthUserService;
import com.team4real.demo.domain.creator.dto.CreatorAnalysisRequestDto;
import com.team4real.demo.domain.creator.dto.CreatorAnalysisResponseDto;
import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.creator.entity.CreatorAnalysis;
import com.team4real.demo.domain.creator.repository.CreatorAnalysisRepository;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreatorAnalysisService {
    private final CreatorAnalysisRepository creatorAnalysisRepository;
    private final AuthUserService authUserService;

    public void saveAnalysisInfo(CreatorAnalysisRequestDto request) {
        Creator creator = authUserService.getCurrentCreatorUser();
        CreatorAnalysis entity = CreatorAnalysis.builder()
                .creator(creator)
                .interestCategories(request.interestCategories())
                .brandThemes(request.brandThemes())
                .skinType(request.skinType())
                .skinTone(request.skinTone())
                .makeupStyle(request.makeupStyle())
                .sns(request.sns())
                .targetGender(request.targetGender())
                .targetAgeGroup(request.targetAgeGroup())
                .contentTypes(request.contentTypes())
                .contentTopics(request.contentTopics())
                .contentLength(request.contentLength())
                .brandPreferences(request.brandPreferences())
                .build();

        creatorAnalysisRepository.findByCreator(creator)
                .ifPresentOrElse(
                        existing -> {
                            entity.setCreatorAnalysisId(existing.getCreatorAnalysisId());
                            creatorAnalysisRepository.save(entity);
                        }, () -> creatorAnalysisRepository.save(entity)
                );
    }

    @Transactional(readOnly = true)
    public CreatorAnalysisResponseDto getAnalysisResult() {
        Creator creator = authUserService.getCurrentCreatorUser();
        CreatorAnalysis a = creatorAnalysisRepository.findByCreator(creator)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        return CreatorAnalysisResponseDto.from(
                "○○한 크리에이터입니다.",
                a.getInterestCategories().subList(0, Math.min(2, a.getInterestCategories().size())),
                a.getContentTypes().subList(0, Math.min(2, a.getContentTypes().size())),
                a.getTargetAgeGroup() + " 고객 도달률이 높습니다."
        );
    }
}
