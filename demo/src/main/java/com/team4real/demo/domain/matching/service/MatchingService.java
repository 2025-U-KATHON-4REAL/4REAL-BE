package com.team4real.demo.domain.matching.service;

import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.domain.auth.service.AuthUserService;
import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.brand.repository.BrandLikeRepository;
import com.team4real.demo.domain.chat.entity.ChatRoom;
import com.team4real.demo.domain.chat.repository.ChatRoomRepository;
import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.matching.dto.BrandUnitDto;
import com.team4real.demo.domain.matching.dto.BrandUnitRequestDto;
import com.team4real.demo.domain.matching.dto.CreatorUnitDto;
import com.team4real.demo.domain.matching.dto.MatchingDataDto;
import com.team4real.demo.domain.matching.entity.Matching;
import com.team4real.demo.domain.matching.entity.MatchingSortStrategy;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import com.team4real.demo.domain.matching.repository.MatchingRepository;
import com.team4real.demo.global.common.dto.PageResult;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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
    private final ChatRoomRepository chatRoomRepository;

    // 크리에이터 회원을 위한
    @Transactional(readOnly = true)
    public PageResult<BrandUnitDto> getRecommendedMatchingForCreatorUserWithCursor(
            MatchingSortStrategy sort, int size, Long lastMatchingId) {
        Creator creator = authUserService.getCurrentCreatorUser();
        return getCursorMatchings(
                () -> matchingRepository.findCreatorMatchingsWithCursor(creator, MatchingStatus.RECOMMENDED, lastMatchingId,
                        size,
                        sort),
                m -> {
                    Brand brand = m.getBrand();
                    boolean liked = brandLikeRepository.existsByAuthUserAndBrand(creator.getAuthUser(), brand);
                    return BrandUnitDto.from(m, brand, liked);
                },
                size
        );
    }

    // 크리에이터 회원을 위한
    @Transactional(readOnly = true)
    public PageResult<BrandUnitRequestDto> getPendingMatchingForCreatorUserWithCursor(
            MatchingSortStrategy sort, int size, Long lastMatchingId) {
        Creator creator = authUserService.getCurrentCreatorUser();
        return getCursorMatchings(
                () -> matchingRepository.findCreatorMatchingsWithCursor(creator, MatchingStatus.PENDING, lastMatchingId,
                        size,
                        sort),
                m -> {
                    Brand brand = m.getBrand();
                    boolean liked = brandLikeRepository.existsByAuthUserAndBrand(creator.getAuthUser(), brand);
                    return BrandUnitRequestDto.from(m, brand, liked);
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
                () -> matchingRepository.findBrandMatchingsWithCursor(brand, status, lastMatchingId,
                        size,
                        sort),
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
    public void pendMatching(Long matchingId, MatchingDataDto matchingDataDto) {
        AuthUser authUser = authUserService.getCurrentAuthUser();
        Matching matching = matchingRepository.findById(matchingId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        validateReceiver(authUser, matching);
        matching.pend(matchingDataDto.content());
        chatRoomRepository.findByMatching_MatchingId(matchingId)
                .orElseGet(() -> chatRoomRepository.save(new ChatRoom(matching)));
    }

    // 매칭 수락 & 채팅방 생성
    @Transactional
    public void acceptMatching(Long matchingId, MatchingDataDto matchingDataDto) {
        AuthUser authUser = authUserService.getCurrentAuthUser();
        Matching matching = matchingRepository.findById(matchingId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        validateReceiver(authUser, matching);
        matching.accept(matchingDataDto.content());
    }

    @Transactional
    public void rejectMatching(Long matchingId, MatchingDataDto matchingDataDto) {
        AuthUser authUser = authUserService.getCurrentAuthUser();
        Matching matching = matchingRepository.findById(matchingId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        validateReceiver(authUser, matching);
        matching.reject(matchingDataDto.content());
    }

    private void validateReceiver(AuthUser authUser, Matching matching) {
        // 요청자는 권한 없음
        if (matching.getInitiator().equals(authUser.getRole())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
        }
        // 수신자가 본인인지 확인
        boolean isReceiver = false;
        if (authUser.isCreator() && matching.getCreator().getAuthUser().equals(authUser)) {
            isReceiver = true;
        } else if (authUser.isBrand() && matching.getBrand().getAuthUser().equals(authUser)) {
            isReceiver = true;
        }
        if (!isReceiver) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
        }
    }
}
