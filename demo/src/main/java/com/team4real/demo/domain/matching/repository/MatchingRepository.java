package com.team4real.demo.domain.matching.repository;

import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
    @Query("SELECT m FROM Matching m JOIN FETCH m.brand WHERE m.creator = :creator AND m.status = :status AND m.matchingId < :lastId ORDER BY m.matchingId DESC")
    List<Matching> findWithBrandByCursor(@Param("creator") Creator creator, @Param("status") MatchingStatus status, @Param("lastId") Long lastId, Pageable pageable);
    @Query("""
    SELECT m FROM Matching m
    JOIN FETCH m.creator
    WHERE m.brand = :brand AND m.status = :status
    AND (:lastMatchingId IS NULL OR m.matchingId < :lastMatchingId)
    ORDER BY m.matchingId DESC
    """)
    List<Matching> findByBrandAndStatusWithCursor(
            @Param("brand") Brand brand,
            @Param("status") MatchingStatus status,
            @Param("lastMatchingId") Long lastMatchingId,
            Pageable pageable);
}
