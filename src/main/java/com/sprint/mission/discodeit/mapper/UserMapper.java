package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

    @Autowired
    protected BinaryContentMapper binaryContentMapper;

    @Mapping(target = "username", source = "name")
    @Mapping(target = "online", expression = "java(user.getStatus() != null && user.getStatus().isOnline())")
    public abstract UserDto toDto(User user);

    @Mapping(target = "username", source = "user.name")
    @Mapping(target = "online", source = "isOnline")
    public abstract UserDto toDto(User user, boolean isOnline);
}
