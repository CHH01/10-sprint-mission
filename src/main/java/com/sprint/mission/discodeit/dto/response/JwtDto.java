package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.dto.data.UserDto;

public record JwtDto(
        UserDto user,
        String accessToken
) {
}
