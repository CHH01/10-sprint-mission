package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public interface MessageService {
    MessageResponse createMessage(MessageCreateRequest request, List<MultipartFile> files);

    MessageResponse getMessage(UUID id);

    List<MessageResponse> getAllMessages();

    List<MessageResponse> findAllByChannelId(UUID channelId);

    MessageResponse updateMessage(UUID messageId, MessageUpdateRequest request);

    void deleteMessage(UUID id);

    List<MessageResponse> getMessagesByUserId(UUID userId);
}