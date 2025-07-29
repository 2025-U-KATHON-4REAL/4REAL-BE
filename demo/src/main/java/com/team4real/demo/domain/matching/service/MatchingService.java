package com.team4real.demo.domain.matching.service;

import com.team4real.demo.domain.auth.service.AuthUserService;
import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.matching.dto.MatchingBrandsListResponseDto;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import com.team4real.demo.domain.matching.repository.MatchingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final MatchingRepository matchingRepository;
    private final AuthUserService authUserService;

    public Page<MatchingBrandsListResponseDto> getMatchingForCreatorUser(MatchingStatus status, Pageable pageable) {
        Creator creator = authUserService.getCurrentCreatorUser();
        return matchingRepository.findByCreatorAndStatus(creator, status, pageable)
                .map(matching -> MatchingBrandsListResponseDto.from(matching, matching.getBrand()));
    }
    public Page<MatchingBrandsListResponseDto> getMatchingForBrandUser(MatchingStatus status, Pageable pageable) {
        Brand brand = authUserService.getCurrentBrandUser();
        return matchingRepository.findByBrandAndStatus(brand, status, pageable)
                .map(matching -> MatchingBrandsListResponseDto.from(matching, matching.getBrand()));
    }
}
