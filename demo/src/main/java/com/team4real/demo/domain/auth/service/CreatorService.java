package com.team4real.demo.domain.auth.service;

import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.domain.creator.entity.Creator;
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
public class CreatorService {
    private final CreatorRepository creatorRepository;
}
