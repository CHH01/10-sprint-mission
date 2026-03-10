package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Transactional
    public UserStatusDto create(UserStatusCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        UserStatus userStatus = userStatusRepository.findByUser_Id(request.getUserId())
                .map(existing -> {
                    existing.updateLastActiveAt(Instant.now());
                    return existing;
                })
                .orElseGet(() -> {
                    UserStatus newUserStatus = new UserStatus(user, Instant.now());
                    return userStatusRepository.save(newUserStatus);
                });

        return userStatusMapper.toDto(userStatus);
    }

    public UserStatusDto find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 상태입니다."));
        return userStatusMapper.toDto(userStatus);
    }

    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserStatusDto update(UserStatusUpdateRequest request) {
        return null;
    }

    @Transactional
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 상태입니다."));
        
        if (request.getNewLastActiveAt() != null) {
            userStatus.updateLastActiveAt(request.getNewLastActiveAt());
        } else {
            userStatus.updateLastActiveAt(Instant.now());
        }
        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }
}
