package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;
  private final BinaryContentStorage binaryContentStorage;
  private final ReadStatusRepository readStatusRepository;

  @Override
  @Transactional
  public MessageDto createMessage(MessageCreateRequest request, List<MultipartFile> files) {
    Channel channel = channelRepository.findById(request.getChannelId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    User user = userRepository.findById(request.getAuthorId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

    if (readStatusRepository.findByUser_IdAndChannel_Id(user.getId(), channel.getId()).isEmpty()) {
      if (ChannelType.PUBLIC.equals(channel.getType())) {
        readStatusRepository.save(new ReadStatus(user, channel));
      } else {
        throw new IllegalArgumentException("채널에 먼저 입장해야 메시지를 남길 수 있습니다.");
      }
    }

    List<BinaryContent> attachments = saveBinaryContents(files);

    Message message = new Message(channel, user, request.getContent());
    attachments.forEach(message::addAttachment);

    messageRepository.save(message);

    return messageMapper.toDto(message);
  }

  @Override
  public MessageDto getMessage(UUID id) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
    return messageMapper.toDto(message);
  }

  @Override
  public List<MessageDto> getAllMessages() {
    return messageRepository.findAll().stream()
        .map(messageMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public Slice<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size) {
    Pageable pageable = PageRequest.of(0, size, Sort.by("createdAt").descending());
    if (cursor != null) {
      return messageRepository.findByChannel_IdAndCreatedAtBefore(channelId, cursor, pageable)
          .map(messageMapper::toDto);
    }
    return messageRepository.findByChannel_Id(channelId, pageable)
        .map(messageMapper::toDto);
  }

  @Override
  @Transactional
  public MessageDto updateMessage(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));

    if (request.getNewContent() != null && !request.getNewContent().isBlank()) {
      message.updateContent(request.getNewContent());
    }

    return messageMapper.toDto(message);
  }

  @Transactional
  protected List<BinaryContent> saveBinaryContents(List<MultipartFile> files) {
    List<BinaryContent> attachments = new ArrayList<>();
    if (files != null) {
      for (MultipartFile file : files) {
        if (!file.isEmpty()) {
          try {
            BinaryContent binaryContent = new BinaryContent(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
            );
            binaryContentStorage.put(binaryContent.getId(), file.getBytes());
            attachments.add(binaryContent);
          } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
          }
        }
      }
    }
    return attachments;
  }

  @Override
  @Transactional
  public void deleteMessage(UUID id) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));

    for (BinaryContent attachment : new ArrayList<>(message.getAttachments())) {
      binaryContentRepository.delete(attachment);
    }

    messageRepository.delete(message);
  }

  @Override
  public List<MessageDto> getMessagesByUserId(UUID userId) {
    return messageRepository.findAllByAuthor_Id(userId).stream()
        .map(messageMapper::toDto)
        .collect(Collectors.toList());
  }
}
