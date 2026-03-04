package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class PrivateChannelCreateRequest {
    private String name;
    private String description;
    private List<UUID> participantIds;

    public PrivateChannelCreateRequest(String name, String description, List<UUID> participantIds) {
        this.name = name;
        this.description = description;
        this.participantIds = participantIds;
    }
}
