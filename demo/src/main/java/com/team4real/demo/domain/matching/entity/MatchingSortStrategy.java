package com.team4real.demo.domain.matching.entity;

import org.springframework.data.domain.Sort;

public enum MatchingSortStrategy {
    BEST_MATCH,     // 매칭률순
    POPULARITY,     // 인기순
    LATEST          // 등록순
    ;

    public Sort toSort() {
        return switch (this) {
            case BEST_MATCH -> Sort.by(Sort.Direction.DESC, "matchScore");
            case POPULARITY -> Sort.by(Sort.Direction.DESC, "popularity");
            case LATEST -> Sort.by(Sort.Direction.DESC, "matchingId");
        };
    }
}