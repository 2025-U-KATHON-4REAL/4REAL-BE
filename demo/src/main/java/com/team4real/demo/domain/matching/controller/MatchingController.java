package com.team4real.demo.domain.matching.controller;

import com.team4real.demo.domain.matching.dto.MatchingBrandsListResponseDto;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import com.team4real.demo.domain.matching.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @Operation(summary = "크리에이터 - 대기 중인 매칭 확인")
    @PreAuthorize("hasRole('CREATOR')")
    @GetMapping("/brands/matchings")
    public ResponseEntity<Page<MatchingBrandsListResponseDto>> getMatchingsForCreator(
            @RequestParam(defaultValue = "PENDING") MatchingStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<MatchingBrandsListResponseDto> result = matchingService.getMatchingForCreatorUser(status, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "브랜드 - 대기 중인 매칭 확인")
    @PreAuthorize("hasRole('BRAND')")
    @GetMapping("/creators/matchings")
    public ResponseEntity<Page<MatchingBrandsListResponseDto>> getMatchingsForBrand(
            @RequestParam(defaultValue = "PENDING") MatchingStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<MatchingBrandsListResponseDto> result = matchingService.getMatchingForBrandUser(status, pageable);
        return ResponseEntity.ok(result);
    }
}
