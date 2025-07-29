package com.team4real.demo.domain.auth.service;

import com.team4real.demo.domain.auth.dto.AuthMeResponseDto;
import com.team4real.demo.domain.auth.dto.AuthProfileResponseDto;
import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.domain.auth.entity.Role;
import com.team4real.demo.domain.auth.repository.AuthUserRepository;
import com.team4real.demo.domain.brand.repository.BrandRepository;
import com.team4real.demo.domain.creator.repository.CreatorRepository;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthUserService {
    private final AuthUserRepository authUserRepository;
    private final CreatorRepository creatorRepository;
    private final BrandRepository brandRepository;

    public AuthUser createAuthUser(String email, String encryptedPassword, Role role, String phoneNumber) throws CustomException {
        AuthUser authUser = AuthUser.builder()
                .email(email)
                .passwordHash(encryptedPassword)
                .role(role)
                .phoneNumber(phoneNumber)
                .build();
        return authUserRepository.save(authUser);
    }

    public void createCreator(AuthUser authUser, String name) {
        Creator creator = Creator.builder()
                .authUser(authUser)
                .name(name)
                .build();
        creatorRepository.save(creator);
    }

    public void createBrand(AuthUser authUser, String name) {
        Brand brand = Brand.builder()
                .authUser(authUser)
                .name(name)
                .build();
        brandRepository.save(brand);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return authUserRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public AuthUser getAuthUserByEmail(String email) {
        return authUserRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public AuthUser getCurrentAuthUser() throws CustomException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authUserRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_AUTHENTICATED));
    }

    @Transactional(readOnly = true)
    public Brand getCurrentBrandUser() {
        AuthUser authUser = getCurrentAuthUser();
        return brandRepository.findByAuthUser(authUser)
                .orElseThrow(() -> new IllegalStateException("Brand가 아닙니다."));
    }

    @Transactional(readOnly = true)
    public Creator getCurrentCreatorUser() {
        AuthUser authUser = getCurrentAuthUser();
        return creatorRepository.findByAuthUser(authUser)
                .orElseThrow(() -> new IllegalStateException("Creator가 아닙니다."));
    }

    @Transactional(readOnly = true)
    public AuthMeResponseDto getCurrentMe() {
        AuthUser authUser = getCurrentAuthUser();
        Long refId = switch (authUser.getRole()) {
            case CREATOR -> creatorRepository.findByAuthUser(authUser)
                    .map(Creator::getCreatorId)
                    .orElseThrow(() -> new IllegalStateException("Creator가 아닙니다."));
            case BRAND -> brandRepository.findByAuthUser(authUser)
                    .map(Brand::getBrandId)
                    .orElseThrow(() -> new IllegalStateException("Brand가 아닙니다."));
            default -> null;
        };
        return AuthMeResponseDto.from(authUser, refId);
    }

    private record ProfileInfo(String name, String image) {}

    @Transactional(readOnly = true)
    public AuthProfileResponseDto getCurrentProfile() {
        AuthUser authUser = getCurrentAuthUser();
        ProfileInfo profileInfo = switch (authUser.getRole()) {
            case CREATOR -> creatorRepository.findByAuthUser(authUser)
                    .map(c -> new ProfileInfo(c.getName(), c.getImage()))
                    .orElseThrow(() -> new IllegalStateException("Creator가 아닙니다."));
            case BRAND -> brandRepository.findByAuthUser(authUser)
                    .map(b -> new ProfileInfo(b.getName(), b.getImage()))
                    .orElseThrow(() -> new IllegalStateException("Brand가 아닙니다."));
            default -> new ProfileInfo("ADMIN", null);
        };
        return AuthProfileResponseDto.from(profileInfo.name(), profileInfo.image(), authUser.getEmail());
    }
}
