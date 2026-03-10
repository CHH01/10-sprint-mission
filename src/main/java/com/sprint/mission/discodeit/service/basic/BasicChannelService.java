package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ChannelMapper channelMapper;
  private final BasicAuthService authService;

  @Override
  @Transactional
  public ChannelDto createPublicChannel(PublicChannelCreateRequest request) {
    if (channelRepository.existsByName(request.getName())) {
      throw new IllegalArgumentException("이미 존재하는 채널 이름입니다: " + request.getName());
    }

    Channel channel = new Channel(request.getName(), ChannelType.PUBLIC, request.getDescription());
    channelRepository.save(channel);
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional
  public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {
    String defaultName = "Private Channel " + UUID.randomUUID().toString().substring(0, 8);
    Channel channel = new Channel(defaultName, ChannelType.PRIVATE, "Private message channel");
    channelRepository.save(channel);

    Set<UUID> participantIds = new HashSet<>();
    if (request.getParticipantIds() != null) {
      participantIds.addAll(request.getParticipantIds());
    }

    UserDto currentUser = authService.getCurrentUser();
    if (currentUser != null) {
      participantIds.add(currentUser.id());
    }

    for (UUID userId : participantIds) {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다: " + userId));

      ReadStatus readStatus = new ReadStatus(user, channel);
      readStatusRepository.save(readStatus);
      channel.addReadStatus(readStatus);
      user.addReadStatus(readStatus);
    }
    return channelMapper.toDto(channel);
  }

  @Override
  public ChannelDto getChannel(UUID id) {
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    return channelMapper.toDto(channel);
  }

  @Override
  public List<ChannelDto> getAllChannels() {
    return channelRepository.findAll().stream()
        .map(channelMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new IllegalArgumentException("존재하지 않는 유저입니다.");
    }

    return channelRepository.findAll().stream()
        .filter(channel -> canUserAccessChannel(channel, userId))
        .map(channelMapper::toDto)
        .sorted(Comparator.comparing(ChannelDto::lastMessageAt,
            Comparator.nullsLast(Comparator.reverseOrder())))
        .collect(Collectors.toList());
  }

  private boolean canUserAccessChannel(Channel channel, UUID userId) {
    if (ChannelType.PUBLIC.equals(channel.getType())) {
      return true;
    }
    return readStatusRepository.findByUser_IdAndChannel_Id(userId, channel.getId()).isPresent();
  }

  @Override
  @Transactional
  public ChannelDto updateChannel(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

    if (ChannelType.PRIVATE.equals(channel.getType())) {
      throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
    }

    if (request.getNewName() != null && !request.getNewName().isBlank()) {
        channel.updateName(request.getNewName());
    }
    if (request.getNewDescription() != null) {
        channel.updateDescription(request.getNewDescription());
    }

    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional
  public void deleteChannel(UUID id) {
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

    channelRepository.delete(channel);
  }

  @Override
  @Transactional
  public ChannelDto enterChannel(UUID userId, UUID channelId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

    if (ChannelType.PRIVATE.equals(channel.getType())) {
      throw new IllegalStateException("PRIVATE 채널은 초대 없이 입장할 수 없습니다.");
    }

    if (readStatusRepository.findByUser_IdAndChannel_Id(userId, channelId).isPresent()) {
      throw new IllegalArgumentException("이미 해당 채널에 참가 중입니다.");
    }

    ReadStatus readStatus = new ReadStatus(user, channel);
    readStatusRepository.save(readStatus);
    channel.addReadStatus(readStatus);
    user.addReadStatus(readStatus);

    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional
  public void leaveChannel(UUID userId, UUID channelId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

    List<Message> userMessages = messageRepository.findAllByAuthor_Id(userId).stream()
        .filter(m -> m.getChannel().getId().equals(channelId))
        .toList();
    messageRepository.deleteAll(userMessages);

    readStatusRepository.findByUser_IdAndChannel_Id(userId, channelId)
        .ifPresent(readStatusRepository::delete);
  }
}
