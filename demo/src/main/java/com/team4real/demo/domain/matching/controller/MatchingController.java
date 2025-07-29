package com.team4real.demo.domain.matching.controller;

import com.team4real.demo.domain.matching.dto.MatchingBrandsListResponseDto;
import com.team4real.demo.domain.matching.dto.MatchingCreatorsListResponseDto;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import com.team4real.demo.domain.matching.service.MatchingService;
import com.team4real.demo.global.common.dto.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Matching")
@RestController
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @Operation(summary = "크리에이터 시점의 브랜 매칭 리스트 (무한 스크롤)")
    @PreAuthorize("hasRole('CREATOR')")
    @GetMapping("/creators/matchings")
    public ResponseEntity<PageResult<MatchingBrandsListResponseDto>> getCursorMatchingsForCreator(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "PENDING") MatchingStatus status,
            @RequestParam(required = false) Long lastMatchingId
    ) {
        PageResult<MatchingBrandsListResponseDto> result = matchingService.getCursorMatchingForCreatorUser(status, lastMatchingId, size);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "브랜드 시점의 크리에이터 매칭 리스트 (무한 스크롤)")
    @PreAuthorize("hasRole('BRAND')")
    @GetMapping("/brands/matchings")
    public ResponseEntity<PageResult<MatchingCreatorsListResponseDto>> getMatchingsForBrand(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "PENDING") MatchingStatus status,
            @RequestParam(required = false) Long lastMatchingId
    ) {
        PageResult<MatchingCreatorsListResponseDto> result = matchingService.getMatchingForBrandUserWithCursor(status, lastMatchingId, size);
        return ResponseEntity.ok(result);
    }

}
