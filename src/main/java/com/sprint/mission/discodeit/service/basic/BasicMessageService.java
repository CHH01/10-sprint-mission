package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageResponse createMessage(MessageCreateRequest request, List<MultipartFile> files) {
        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
        User user = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!channel.getUsers().contains(user)) {
            throw new IllegalArgumentException("채널에 먼저 입장해야 메시지를 남길 수 있습니다.");
        }

        List<UUID> attachmentIds = saveBinaryContents(files);

        Message message = new Message(request.getChannelId(), request.getAuthorId(), request.getContent(), attachmentIds);

        channel.addMessage(message);
        user.addMessage(message);

        messageRepository.save(message);
        channelRepository.save(channel);
        userRepository.save(user);

        return messageMapper.toResponse(message);
    }

    @Override
    public MessageResponse getMessage(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
        return messageMapper.toResponse(message);
    }

    @Override
    public List<MessageResponse> getAllMessages() {
        return messageRepository.findAll().stream()
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponse updateMessage(MessageUpdateRequest request, List<MultipartFile> files) {
        Message message = messageRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
        
        if (request.getContent() != null && !request.getContent().isBlank()) {
            message.updateContent(request.getContent());
        }

        if (files != null && !files.isEmpty()) {
            if (message.getAttachmentIds() != null) {
                for (UUID attachmentId : message.getAttachmentIds()) {
                    binaryContentRepository.delete(attachmentId);
                }
            }

            List<UUID> newAttachmentIds = saveBinaryContents(files);
            message.updateAttachments(newAttachmentIds);
        }

        messageRepository.save(message);
        return messageMapper.toResponse(message);
    }

    private List<UUID> saveBinaryContents(List<MultipartFile> files) {
        List<UUID> attachmentIds = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        BinaryContent binaryContent = new BinaryContent(
                                file.getBytes(),
                                file.getOriginalFilename(),
                                file.getContentType()
                        );
                        binaryContentRepository.save(binaryContent);
                        attachmentIds.add(binaryContent.getId());
                    } catch (IOException e) {
                        throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
                    }
                }
            }
        }
        return attachmentIds;
    }

    @Override
    public void deleteMessage(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
        
        if (message.getAttachmentIds() != null) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.delete(attachmentId);
            }
        }

        channelRepository.findById(message.getChannelId()).ifPresent(channel -> {
            channel.getMessages().removeIf(m -> m.getId().equals(id));
            channelRepository.save(channel);
        });

        userRepository.findById(message.getAuthorId()).ifPresent(user -> {
            user.getMessages().removeIf(m -> m.getId().equals(id));
            userRepository.save(user);
        });

        messageRepository.delete(id);
    }

    @Override
    public List<MessageResponse> getMessagesByUserId(UUID userId) {
        return messageRepository.findAll().stream()
                .filter(message -> message.getAuthorId().equals(userId))
                .map(messageMapper::toResponse)
                .collect(Collectors.toList());
    }
}
