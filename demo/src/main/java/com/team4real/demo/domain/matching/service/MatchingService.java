package com.team4real.demo.domain.matching.service;

import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.domain.auth.service.AuthUserService;
import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.brand.repository.BrandLikeRepository;
import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.matching.dto.BrandUnitDto;
import com.team4real.demo.domain.matching.dto.CreatorUnitDto;
import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.domain.matching.entity.MatchingSortStrategy;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import com.team4real.demo.domain.matching.repository.MatchingRepository;
import com.team4real.demo.global.common.dto.PageResult;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
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
    public PageResult<BrandUnitDto> getMatchingForCreatorUserWithCursor(
            MatchingStatus status, MatchingSortStrategy sort, int size, Long lastMatchingId) {
        Creator creator = authUserService.getCurrentCreatorUser();
        return getCursorMatchings(
                () -> matchingRepository.findWithBrandByCursor(creator, status, lastMatchingId, PageRequest.of(0, size + 1, sort.toSort())),
                m -> {
                    Brand brand = m.getBrand();
                    boolean liked = brandLikeRepository.existsByAuthUserAndBrand(creator.getAuthUser(), brand);
                    return BrandUnitDto.from(m, brand, liked);
                },
                size
        );
    }

    // 기업 회원을 위한
    @Transactional(readOnly = true)
    public PageResult<CreatorUnitDto> getMatchingForBrandUserWithCursor(
            MatchingStatus status, MatchingSortStrategy sort, int size, Long lastMatchingId) {
        Brand brand = authUserService.getCurrentBrandUser();
        return getCursorMatchings(
                () -> matchingRepository.findByBrandAndStatusWithCursor(brand, status, lastMatchingId, PageRequest.of(0, size + 1, sort.toSort())),
                m -> {
                    Creator creator = m.getCreator();
                    return CreatorUnitDto.from(m, creator, false);
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

    @Transactional
    public void acceptMatching(Long matchingId) {
        Matching matching = matchingRepository.findById(matchingId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        validateMatchingOwnership(matching); // 본인 확인 로직
        matching.accept();
    }

    @Transactional
    public void rejectMatching(Long matchingId) {
        Matching matching = matchingRepository.findById(matchingId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        validateMatchingOwnership(matching);
        matching.reject();
    }

    public void validateMatchingOwnership(Matching matching) {
        AuthUser user = authUserService.getCurrentAuthUser();
        boolean isOwner = false;
        if (user.isCreator()) {
            isOwner = matching.getCreator().getAuthUser().equals(user);
        } else if (user.isBrand()) {
            isOwner = matching.getBrand().getAuthUser().equals(user);
        }
        if (!isOwner) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
        }
    }
}
