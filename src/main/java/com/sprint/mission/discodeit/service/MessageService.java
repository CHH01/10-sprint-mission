package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;

public interface MessageService {
    MessageDto createMessage(MessageCreateRequest request, List<MultipartFile> files);

    MessageDto getMessage(UUID id);

    List<MessageDto> getAllMessages();

    Slice<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size);

    MessageDto updateMessage(UUID messageId, MessageUpdateRequest request);

    void deleteMessage(UUID id);

    List<MessageDto> getMessagesByUserId(UUID userId);
}