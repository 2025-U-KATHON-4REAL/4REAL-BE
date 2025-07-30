package com.team4real.demo.domain.creator.repository;

import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.creator.entity.CreatorAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreatorAnalysisRepository extends JpaRepository<CreatorAnalysis, Long> {
    Optional<CreatorAnalysis> findByCreator(Creator creator);
}