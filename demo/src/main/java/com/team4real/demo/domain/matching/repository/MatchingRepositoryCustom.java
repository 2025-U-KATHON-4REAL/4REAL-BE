package com.team4real.demo.domain.matching.repository;

import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.domain.matching.entity.MatchingSortStrategy;
import com.team4real.demo.domain.matching.entity.MatchingStatus;

import java.util.List;

public interface MatchingRepositoryCustom {
    List<Matching> findCreatorMatchingsWithCursor( // 크리에이터가 보는 매칭 목록
            Creator creator, MatchingStatus status, Long lastMatchingId, int size, MatchingSortStrategy sort);

    List<Matching> findBrandMatchingsWithCursor( // 브랜드가 보는 매칭 목록
            Brand brand, MatchingStatus status, Long lastMatchingId, int size, MatchingSortStrategy sort);
}
