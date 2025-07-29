package com.team4real.demo.domain.matching.repository;

import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
    Page<Matching> findByBrandAndStatus(Brand brand, MatchingStatus status, Pageable pageable);
    Page<Matching> findByCreatorAndStatus(Creator creator, MatchingStatus status, Pageable pageable);
}
