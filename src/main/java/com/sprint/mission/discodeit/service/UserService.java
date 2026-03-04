package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto createUser(UserCreateRequest request, MultipartFile file);
    UserDto getUser(UUID id);
    List<UserDto> getAllUsers();
    UserDto updateUser(UUID userId, UserUpdateRequest request, MultipartFile file);
    void deleteUser(UUID id);
}