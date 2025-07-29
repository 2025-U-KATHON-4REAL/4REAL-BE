package com.team4real.demo.domain.chat.service;


import com.team4real.demo.domain.auth.entity.AuthUser;
import com.team4real.demo.domain.auth.entity.Role;
import com.team4real.demo.domain.auth.service.AuthUserService;
import com.team4real.demo.domain.brand.entity.Brand;
import com.team4real.demo.domain.chat.dto.ChatRoomPreviewDto;
import com.team4real.demo.domain.chat.entity.ChatRoom;
import com.team4real.demo.domain.chat.repository.ChatRoomRepository;
import com.team4real.demo.domain.creator.entity.Creator;
import com.team4real.demo.domain.matching.entity.MatchingStatus;
import com.team4real.demo.global.exception.CustomException;
import com.team4real.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final AuthUserService authUserService;

    public List<ChatRoomPreviewDto> getMyChatRooms(MatchingStatus status, boolean sent) {
        AuthUser user = authUserService.getCurrentAuthUser();
        Role myRole = user.getRole();
        Role initiator = sent ? myRole : (myRole == Role.CREATOR ? Role.BRAND : Role.CREATOR);

        if (myRole == Role.CREATOR) {
            Creator creator = authUserService.getCurrentCreatorUser();
            List<ChatRoom> chatRooms;
            if (status == null) {
                chatRooms = chatRoomRepository.findByMatching_CreatorAndMatching_Initiator(creator, initiator);
            } else {
                chatRooms = chatRoomRepository.findByMatching_CreatorAndMatching_InitiatorAndMatching_Status(creator, initiator, status);
            }
            return chatRooms.stream().map(ChatRoomPreviewDto::from).toList();
        } else if (myRole == Role.BRAND) {
            Brand brand = authUserService.getCurrentBrandUser();
            List<ChatRoom> chatRooms;
            if (status == null) {
                chatRooms = chatRoomRepository.findByMatching_BrandAndMatching_Initiator(brand, initiator);
            } else {
                chatRooms = chatRoomRepository.findByMatching_BrandAndMatching_InitiatorAndMatching_Status(brand, initiator, status);
            }
            return chatRooms.stream().map(ChatRoomPreviewDto::from).toList();
        } else {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
        }
    }

}