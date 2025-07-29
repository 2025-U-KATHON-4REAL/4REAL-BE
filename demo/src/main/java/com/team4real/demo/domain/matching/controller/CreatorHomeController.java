package com.team4real.demo.domain.matching.controller;

import com.team4real.demo.domain.matching.dto.BrandUnitDto;
import com.team4real.demo.domain.matching.entity.MatchingSortStrategy;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import com.team4real.demo.domain.matching.service.MatchingService;
import com.team4real.demo.global.common.dto.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Home-Creator")
@RestController
@PreAuthorize("hasRole('CREATOR')")
@RequestMapping("/creators/matchings")
@RequiredArgsConstructor
public class CreatorHomeController {
    private final MatchingService matchingService;

    @Operation(summary = "크리에이터 시점의 브랜드 추천 리스트 (무한 스크롤)")
    @GetMapping("/recommendations")
    public ResponseEntity<PageResult<BrandUnitDto>> getRecommendedMatchingList(
            @RequestParam(defaultValue = "BEST_MATCH") final MatchingSortStrategy sort,
            @RequestParam(defaultValue = "10") final int size,
            @RequestParam(required = false) final Long lastMatchingId
    ) {
        PageResult<BrandUnitDto> result = matchingService.getMatchingForCreatorUserWithCursor(MatchingStatus.RECOMMENDED, sort, size, lastMatchingId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "크리에이터 시점의 브랜드 요청 리스트 (무한 스크롤)")
    @GetMapping("/requests")
    public ResponseEntity<PageResult<BrandUnitDto>> getRequestedMatchingList(
            @RequestParam(defaultValue = "BEST_MATCH") final MatchingSortStrategy sort,
            @RequestParam(defaultValue = "10") final int size,
            @RequestParam(required = false) final Long lastMatchingId
    ) {
        PageResult<BrandUnitDto> result = matchingService.getMatchingForCreatorUserWithCursor(MatchingStatus.PENDING, sort, size, lastMatchingId);
        return ResponseEntity.ok(result);
    }
}
