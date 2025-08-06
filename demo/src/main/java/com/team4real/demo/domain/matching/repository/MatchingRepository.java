package com.team4real.demo.domain.matching.repository;

import com.team4real.demo.domain.matching.entity.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<Matching, Long>, MatchingRepositoryCustom {
}
