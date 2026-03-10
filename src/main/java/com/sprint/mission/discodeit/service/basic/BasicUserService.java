package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ChannelRepository channelRepository;
  private final UserMapper userMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public UserDto createUser(UserCreateRequest request, MultipartFile file) {
    if (userRepository.existsByName(request.getUsername())) {
      throw new IllegalArgumentException("이미 존재하는 이름입니다: " + request.getUsername());
    }
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + request.getEmail());
    }

    BinaryContent profile = saveBinaryContent(file);

    User user = new User(request.getUsername(), request.getEmail(), request.getPassword(), profile);
    userRepository.save(user);

    UserStatus userStatus = new UserStatus(user, Instant.now());
    userStatusRepository.save(userStatus);

    return toDto(user);
  }

  @Override
  public UserDto getUser(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    return toDto(user);
  }

  @Override
  public List<UserDto> getAllUsers() {
    return userRepository.findAll().stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public UserDto updateUser(UUID userId, UserUpdateRequest request, MultipartFile file) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

    if (request.getNewUsername() != null && !request.getNewUsername().isBlank()) {
      String newName = request.getNewUsername();
      if (!user.getName().equals(newName) && userRepository.existsByName(newName)) {
        throw new IllegalArgumentException("이미 존재하는 이름입니다: " + newName);
      }
      user.updateName(newName);
    }

    if (request.getNewEmail() != null && !request.getNewEmail().isBlank()) {
      String newEmail = request.getNewEmail();
      if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
        throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + newEmail);
      }
      user.updateEmail(newEmail);
    }

    if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
      user.updatePassword(request.getNewPassword());
    }

    if (file != null && !file.isEmpty()) {
      BinaryContent profile = saveBinaryContent(file);
      user.updateProfile(profile);
    }

    return toDto(user);
  }

  @Transactional
  protected BinaryContent saveBinaryContent(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }

    validateContentType(file.getContentType());
    try {
      BinaryContent content = new BinaryContent(
          file.getOriginalFilename(),
          file.getContentType(),
          file.getSize()
      );
      binaryContentStorage.put(content.getId(), file.getBytes());
      return content;
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
    }
  }

  @Override
  @Transactional
  public void deleteUser(UUID id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

    if (user.getProfile() != null) {
      binaryContentRepository.delete(user.getProfile());
    }

    userRepository.delete(user);
  }

  private UserDto toDto(User user) {
    return userMapper.toDto(user);
  }

  private void validateContentType(String contentType) {
    if (contentType == null || !ImageType.isAllowed(contentType)) {
      throw new IllegalArgumentException("허용되지 않는 파일 형식입니다. (허용: jpg, png, gif, webp)");
    }
  }
}
