package com.team4real.demo.domain.matching.repository;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.brand.entity.QBrand;
import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.creator.entity.QCreator;
import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.domain.matching.entity.MatchingSortStrategy;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import com.team4real.demo.domain.matching.entity.QMatching;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MatchingRepositoryImpl implements MatchingRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Matching> findCreatorMatchingsWithCursor(
            Creator creator, MatchingStatus status, Long lastMatchingId, int size, MatchingSortStrategy sort) {
        QMatching m = QMatching.matching;
        QBrand b = QBrand.brand;

        JPQLQuery<Matching> query = queryFactory
                .selectFrom(m)
                .join(m.brand, b).fetchJoin()
                .where(
                        m.creator.eq(creator),
                        m.status.eq(status),
                        lastMatchingId != null ? m.matchingId.lt(lastMatchingId) : null
                )
                .limit(size + 1);

        applySort(query, sort, m, b);

        return query.fetch();
    }

    @Override
    public List<Matching> findBrandMatchingsWithCursor(
            Brand brand, MatchingStatus status, Long lastMatchingId, int size, MatchingSortStrategy sort) {
        QMatching m = QMatching.matching;
        QCreator c = QCreator.creator;

        JPQLQuery<Matching> query = queryFactory
                .selectFrom(m)
                .join(m.creator, c).fetchJoin()
                .where(
                        m.brand.eq(brand),
                        m.status.eq(status),
                        lastMatchingId != null ? m.matchingId.lt(lastMatchingId) : null
                )
                .limit(size + 1);

        applySort(query, sort, m, c);

        return query.fetch();
    }

    private void applySort(JPQLQuery<Matching> query, MatchingSortStrategy sort, QMatching m, QBrand b) {
        switch (sort) {
            case BEST_MATCH -> query.orderBy(m.matchScore.desc());
            case POPULARITY -> query.orderBy(b.likeCount.desc());
            case LATEST -> query.orderBy(m.matchingId.desc());
        }
    }

    private void applySort(JPQLQuery<Matching> query, MatchingSortStrategy sort, QMatching m, QCreator c) {
        switch (sort) {
            case BEST_MATCH -> query.orderBy(m.matchScore.desc());
            case POPULARITY -> query.orderBy(c.likeCount.desc());
            case LATEST -> query.orderBy(m.matchingId.desc());
        }
    }
}
