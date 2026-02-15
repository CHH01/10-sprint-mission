package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PublicChannelCreateRequest {
    private String name;
    private String description;

    public PublicChannelCreateRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
