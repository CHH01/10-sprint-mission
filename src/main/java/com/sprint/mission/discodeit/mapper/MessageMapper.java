package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserMapper.class})
public abstract class MessageMapper {

    @Autowired
    protected UserMapper userMapper;

    @Autowired
    protected BinaryContentMapper binaryContentMapper;

    @Mapping(target = "author", source = "author")
    public abstract MessageDto toDto(Message message);
}
