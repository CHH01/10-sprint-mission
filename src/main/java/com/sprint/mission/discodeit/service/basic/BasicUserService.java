package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserCreateRequest request, MultipartFile file) {
        if (userRepository.findAll().stream().anyMatch(u -> u.getName().equals(request.getName()))) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다: " + request.getName());
        }
        if (userRepository.findAll().stream().anyMatch(u -> u.getEmail().equals(request.getEmail()))) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + request.getEmail());
        }

        UUID profileId = saveBinaryContent(file);

        User user = new User(request.getName(), request.getEmail(), request.getPassword(), profileId);
        userRepository.save(user);

        UserStatus userStatus = new UserStatus(user.getId(), Instant.now());
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
    public UserDto updateUser(UUID userId, UserUpdateRequest request, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.updateName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.updateEmail(request.getEmail());
        }

        if (file != null && !file.isEmpty()) {
            if (user.getProfileId() != null) {
                binaryContentRepository.delete(user.getProfileId());
            }
            UUID profileId = saveBinaryContent(file);
            user.updateProfileId(profileId);
        }

        userRepository.save(user);

        for (Channel c : new ArrayList<>(user.getChannels())) {
            channelRepository.findById(c.getId()).ifPresent(channelRepository::save);
        }
        
        return toDto(user);
    }

    private UUID saveBinaryContent(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateContentType(file.getContentType());
        try {
            BinaryContent content = new BinaryContent(
                    file.getBytes(),
                    file.getOriginalFilename(),
                    file.getContentType()
            );
            binaryContentRepository.save(content);
            return content.getId();
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        for (Message message : new ArrayList<>(user.getMessages())) {
            if (message.getAttachmentIds() != null) {
                message.getAttachmentIds().forEach(binaryContentRepository::delete);
            }
            channelRepository.findById(message.getChannelId()).ifPresent(channel -> {
                channel.removeMessage(message);
                channelRepository.save(channel);
            });
            messageRepository.delete(message.getId());
        }

        for (Channel c : new ArrayList<>(user.getChannels())) {
            channelRepository.findById(c.getId()).ifPresent(channel -> {
                channel.removeUser(user);
                channelRepository.save(channel);
            });
        }

        if (user.getProfileId() != null) {
            binaryContentRepository.delete(user.getProfileId());
        }

        userStatusRepository.findByUserId(id)
                .ifPresent(status -> userStatusRepository.delete(status.getId()));

        userRepository.delete(id);
    }

    private UserDto toDto(User user) {
        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return userMapper.toDto(user, isOnline);
    }

    private void validateContentType(String contentType) {
        if (contentType == null || !ImageType.isAllowed(contentType)) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다. (허용: jpg, png, gif, webp)");
        }
    }
}
