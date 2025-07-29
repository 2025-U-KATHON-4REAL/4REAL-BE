package com.team4real.demo.domain.chat.repository;

import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.domain.auth.entity.Role;
import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.chat.entity.ChatRoom;
import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByMatching_MatchingId(Long matchingId);
    List<ChatRoom> findByMatching_CreatorAndMatching_Initiator(Creator creator, Role initiator); // 보통 이렇게
    List<ChatRoom> findByMatching_CreatorAndMatching_InitiatorAndMatching_Status(Creator creator, Role initiator, MatchingStatus status);
    List<ChatRoom> findByMatching_BrandAndMatching_Initiator(Brand brand, Role initiator); // 보통 이렇게
    List<ChatRoom> findByMatching_BrandAndMatching_InitiatorAndMatching_Status(Brand brand, Role initiator, MatchingStatus status);

}