package com.team4real.demo.domain.creator.controller;

import com.team4real.demo.domain.creator.dto.CreatorAnalysisRequestDto;
import com.team4real.demo.domain.creator.dto.CreatorAnalysisResponseDto;
import com.team4real.demo.domain.creator.service.CreatorAnalysisService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Creator")
@RestController
@PreAuthorize("hasRole('CREATOR')")
@RequestMapping("/creators/analysis")
@RequiredArgsConstructor
public class CreatorAnalysisController {
    private final CreatorAnalysisService creatorAnalysisService;

    @PostMapping
    public ResponseEntity<Void> saveAnalysis(@RequestBody @Valid CreatorAnalysisRequestDto request) {
        creatorAnalysisService.saveAnalysisInfo(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<CreatorAnalysisResponseDto> getAnalysis() {
        return ResponseEntity.ok(creatorAnalysisService.getAnalysisResult());
    }
}