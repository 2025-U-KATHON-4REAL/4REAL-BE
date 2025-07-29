package com.team4real.demo.domain.matching.service;

import com.team4real.demo.domain.auth.service.AuthUserService;
import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.brand.repository.BrandLikeRepository;
import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.matching.dto.MatchingBrandsListResponseDto;
import com.team4real.demo.domain.matching.dto.MatchingCreatorsListResponseDto;
import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.domain.matching.entity.MatchingSortStrategy;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import com.team4real.demo.domain.matching.repository.MatchingRepository;
import com.team4real.demo.global.common.dto.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchingService {
    private final MatchingRepository matchingRepository;
    private final AuthUserService authUserService;
    private final BrandLikeRepository brandLikeRepository;

    // 크리에이터 회원을 위한
    @Transactional(readOnly = true)
    public PageResult<MatchingBrandsListResponseDto> getMatchingForCreatorUserWithCursor(
            MatchingStatus status, MatchingSortStrategy sort, int size, Long lastMatchingId) {
        Creator creator = authUserService.getCurrentCreatorUser();
        return getCursorMatchings(
                () -> matchingRepository.findWithBrandByCursor(creator, status, lastMatchingId, PageRequest.of(0, size + 1, sort.toSort())),
                m -> {
                    Brand brand = m.getBrand();
                    boolean liked = brandLikeRepository.existsByAuthUserAndBrand(creator.getAuthUser(), brand);
                    return MatchingBrandsListResponseDto.from(m, brand, liked);
                },
                size
        );
    }

    // 기업 회원을 위한
    @Transactional(readOnly = true)
    public PageResult<MatchingCreatorsListResponseDto> getMatchingForBrandUserWithCursor(
            MatchingStatus status, MatchingSortStrategy sort, int size, Long lastMatchingId) {
        Brand brand = authUserService.getCurrentBrandUser();
        return getCursorMatchings(
                () -> matchingRepository.findByBrandAndStatusWithCursor(brand, status, lastMatchingId, PageRequest.of(0, size + 1, sort.toSort())),
                m -> {
                    Creator creator = m.getCreator();
                    return MatchingCreatorsListResponseDto.from(m, creator, false);
                },
                size
        );
    }

    private <D> PageResult<D> getCursorMatchings(
            Supplier<List<Matching>> fetchFunction,
            Function<Matching, D> mappingFunction,
            int size
    ) {
        List<Matching> matchings = fetchFunction.get();
        boolean hasNext = matchings.size() > size;
        if (hasNext) matchings.remove(size); // 다음 페이지를 위한 초과 항목 제거
        Long nextKey = hasNext ? matchings.getLast().getMatchingId() : null;
        List<D> items = matchings.stream().map(mappingFunction).toList();
        return new PageResult<>(items, nextKey);
    }
}
