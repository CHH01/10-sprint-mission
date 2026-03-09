package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {

    @Autowired
    protected MessageRepository messageRepository;

    @Autowired
    protected ReadStatusRepository readStatusRepository;

    @Autowired
    protected UserMapper userMapper;

    @Mapping(target = "participants", expression = "java(mapParticipants(channel))")
    @Mapping(target = "lastMessageAt", expression = "java(calculateLastMessageAt(channel))")
    public abstract ChannelDto toDto(Channel channel);

    protected List<UserDto> mapParticipants(Channel channel) {
        if (channel.getUsers() == null) {
            return List.of();
        }
        return channel.getUsers().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    protected Instant calculateLastMessageAt(Channel channel) {
        if (channel.getMessages() == null || channel.getMessages().isEmpty()) {
            return channel.getCreatedAt();
        }
        return channel.getMessages().stream()
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(channel.getCreatedAt());
    }
}
