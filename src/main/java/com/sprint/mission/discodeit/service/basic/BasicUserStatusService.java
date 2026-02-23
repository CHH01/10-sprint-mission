package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    public UserStatusDto create(UserStatusCreateRequest request) {
        if (userRepository.findById(request.getUserId()).isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        UserStatus userStatus = userStatusRepository.findByUserId(request.getUserId())
                .map(existing -> {
                    existing.updateLastSeen(Instant.now());
                    return existing;
                })
                .orElseGet(() -> new UserStatus(request.getUserId(), Instant.now()));

        userStatusRepository.save(userStatus);
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

    public UserStatusDto update(UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 상태입니다."));
        userStatus.updateLastSeen(request.getLastSeen());
        userStatusRepository.save(userStatus);
        return userStatusMapper.toDto(userStatus);
    }

    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 상태입니다."));
        userStatus.updateLastSeen(request.getLastSeen());
        userStatusRepository.save(userStatus);
        return userStatusMapper.toDto(userStatus);
    }

    public void delete(UUID id) {
        userStatusRepository.delete(id);
    }
}