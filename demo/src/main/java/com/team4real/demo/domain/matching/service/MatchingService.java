package com.team4real.demo.domain.matching.service;

import com.team4real.demo.domain.auth.service.AuthUserService;
import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.matching.dto.MatchingBrandsListResponseDto;
import com.team4real.demo.domain.matching.dto.MatchingCreatorsListResponseDto;
import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import com.team4real.demo.domain.matching.repository.MatchingRepository;
import com.team4real.demo.global.common.dto.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchingService {
    private final MatchingRepository matchingRepository;
    private final AuthUserService authUserService;

    @Transactional(readOnly = true)
    public PageResult<MatchingBrandsListResponseDto> getCursorMatchingForCreatorUser(MatchingStatus status, Long lastMatchingId, int size) {
        Creator creator = authUserService.getCurrentCreatorUser(); // 현재 유저 가져오기
        List<Matching> matchings = matchingRepository.findWithBrandByCursor(
                creator, status, lastMatchingId, PageRequest.of(0, size + 1));
        List<MatchingBrandsListResponseDto> items = matchings.stream()
                .map(m -> MatchingBrandsListResponseDto.from(m, m.getBrand()))
                .toList();

        boolean hasNext = matchings.size() > size;
        if (hasNext) matchings.remove(size); // 다음 페이지용 1개 초과분 제거
        Long nextKey = hasNext ? matchings.getLast().getMatchingId() : null;
        return new PageResult<>(items, nextKey);
    }

    @Transactional(readOnly = true)
    public PageResult<MatchingCreatorsListResponseDto> getMatchingForBrandUserWithCursor(
            MatchingStatus status, Long lastMatchingId, int size) {
        Brand brand = authUserService.getCurrentBrandUser();
        List<Matching> matchings = matchingRepository.findByBrandAndStatusWithCursor(
                brand, status, lastMatchingId, PageRequest.of(0, size + 1));
        List<MatchingCreatorsListResponseDto> items = matchings.stream()
                .limit(size)
                .map(m -> MatchingCreatorsListResponseDto.from(m, m.getCreator()))
                .toList();

        boolean hasNext = matchings.size() > size;
        if (hasNext) matchings.remove(size);
        Long nextKey = hasNext ? matchings.getLast().getMatchingId() : null;
        return new PageResult<>(items, nextKey);
    }
}
