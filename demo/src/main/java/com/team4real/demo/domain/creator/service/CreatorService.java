package com.team4real.demo.domain.creator.service;

import com.team4real.demo.domain.creator.repository.CreatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreatorService {
    private final CreatorRepository creatorRepository;
}
