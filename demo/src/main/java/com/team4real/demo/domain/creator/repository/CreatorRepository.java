package com.team4real.demo.domain.creator.repository;

import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.domain.creator.entity.Creator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreatorRepository extends JpaRepository<Creator, Long> {
    Optional<Creator> findByAuthUser(AuthUser authUser);
}
