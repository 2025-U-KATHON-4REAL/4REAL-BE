package com.team4real.demo.domain.matching.controller;

import com.team4real.demo.domain.matching.dto.MatchingCreatorsListResponseDto;
import com.team4real.demo.domain.matching.entity.MatchingSortStrategy;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import com.team4real.demo.domain.matching.service.MatchingService;
import com.team4real.demo.global.common.dto.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "BrandsHome")
@RestController
@PreAuthorize("hasRole('BRAND')")
@RequestMapping("/brands/matchings")
@RequiredArgsConstructor
public class BrandHomeController {
    private final MatchingService matchingService;

    @Operation(summary = "브랜드 시점의 크리에이터 추천 리스트 (무한 스크롤)")
    @GetMapping("/recommendations")
    public ResponseEntity<PageResult<MatchingCreatorsListResponseDto>> getRecommendedMatchingList(
            @RequestParam(defaultValue = "BEST_MATCH") MatchingSortStrategy sort,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long lastMatchingId
    ) {
        PageResult<MatchingCreatorsListResponseDto> result = matchingService.getMatchingForBrandUserWithCursor(MatchingStatus.RECOMMENDED, sort, size, lastMatchingId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "브랜드 시점의 크리에이터 요청 리스트 (무한 스크롤)")
    @GetMapping("/requests")
    public ResponseEntity<PageResult<MatchingCreatorsListResponseDto>> getRequestedMatchingList(
            @RequestParam(defaultValue = "BEST_MATCH") MatchingSortStrategy sort,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long lastMatchingId
    ) {
        PageResult<MatchingCreatorsListResponseDto> result = matchingService.getMatchingForBrandUserWithCursor(MatchingStatus.PENDING, sort, size, lastMatchingId);
        return ResponseEntity.ok(result);
    }
}
