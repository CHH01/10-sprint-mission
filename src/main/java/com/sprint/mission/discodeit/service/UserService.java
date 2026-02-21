package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserCreateRequest request, MultipartFile file);
    UserResponse getUser(UUID id);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(UUID userId, UserUpdateRequest request, MultipartFile file);
    void deleteUser(UUID id);
}