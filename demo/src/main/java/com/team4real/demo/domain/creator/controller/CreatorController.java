package com.team4real.demo.domain.creator.controller;

import com.team4real.demo.domain.creator.service.CreatorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Creator")
@RestController
@PreAuthorize("hasRole('CREATOR')")
@RequestMapping("/creators")
@RequiredArgsConstructor
public class CreatorController {
    private final CreatorService creatorService;

    @GetMapping("/{creatorId}")
    public ResponseEntity<?> getCreator(@PathVariable final Long creatorId) {
        return ResponseEntity.ok().build();
    }
}