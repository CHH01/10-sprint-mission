package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class UserStatusUpdateRequest {
    private UUID id;
    private Instant lastSeen;

    public UserStatusUpdateRequest(UUID id, Instant lastSeen) {
        this.id = id;
        this.lastSeen = lastSeen;
    }
}
