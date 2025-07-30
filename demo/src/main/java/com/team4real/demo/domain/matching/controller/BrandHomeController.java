package com.team4real.demo.domain.matching.controller;

import com.team4real.demo.domain.matching.dto.CreatorUnitDto;
import com.team4real.demo.domain.matching.dto.MatchingDataDto;
import com.team4real.demo.domain.matching.entity.MatchingSortStrategy;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import com.team4real.demo.domain.matching.service.MatchingService;
import com.team4real.demo.global.common.dto.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Matching-Brand")
@RestController
@PreAuthorize("hasRole('BRAND')")
@RequestMapping("/brands/matchings")
@RequiredArgsConstructor
public class BrandHomeController {
    private final MatchingService matchingService;

    @Operation(summary = "브랜드 시점의 크리에이터 추천 리스트 (무한 스크롤)")
    @GetMapping("/recommendations")
    public ResponseEntity<PageResult<CreatorUnitDto>> getRecommendedMatchingList(
            @RequestParam(defaultValue = "BEST_MATCH") final MatchingSortStrategy sort,
            @RequestParam(defaultValue = "10") final int size,
            @RequestParam(required = false) final Long lastMatchingId
    ) {
        PageResult<CreatorUnitDto> result = matchingService.getMatchingForBrandUserWithCursor(MatchingStatus.RECOMMENDED, sort, size, lastMatchingId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "브랜드 시점의 크리에이터 요청 리스트 (무한 스크롤)")
    @GetMapping("/requests")
    public ResponseEntity<PageResult<CreatorUnitDto>> getRequestedMatchingList(
            @RequestParam(defaultValue = "BEST_MATCH") final MatchingSortStrategy sort,
            @RequestParam(defaultValue = "10") final int size,
            @RequestParam(required = false) final Long lastMatchingId
    ) {
        PageResult<CreatorUnitDto> result = matchingService.getMatchingForBrandUserWithCursor(MatchingStatus.PENDING, sort, size, lastMatchingId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "브랜드가 매칭을 제안")
    @PatchMapping("/{matchingId}/pend")
    public ResponseEntity<Void> pendMatching(@PathVariable final Long matchingId, @RequestBody @Valid MatchingDataDto matchingDataDto) {
        matchingService.pendMatching(matchingId, matchingDataDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "브랜드가 매칭을 수락")
    @PatchMapping("/{matchingId}/accept")
    public ResponseEntity<Void> acceptMatching(@PathVariable final Long matchingId, @RequestBody @Valid MatchingDataDto matchingDataDto) {
        matchingService.acceptMatching(matchingId, matchingDataDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "브랜드가 매칭을 거절")
    @PatchMapping("/{matchingId}/reject")
    public ResponseEntity<Void> rejectMatching(@PathVariable final Long matchingId, @PathVariable @Valid MatchingDataDto matchingDataDto) {
        matchingService.rejectMatching(matchingId, matchingDataDto);
        return ResponseEntity.ok().build();
    }
}
