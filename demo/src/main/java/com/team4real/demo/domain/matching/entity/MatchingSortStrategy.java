package com.team4real.demo.domain.matching.entity;

import org.springframework.data.domain.Sort;

public enum MatchingSortStrategy {
    BEST_MATCH,     // 매칭률순
    POPULARITY,     // 좋아요순
    LATEST          // 등록순
    ;
}