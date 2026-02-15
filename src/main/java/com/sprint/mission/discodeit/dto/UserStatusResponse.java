package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString
public class UserStatusResponse {
    private UUID id;
    private UUID userId;
    private Instant lastSeen;
    private boolean isOnline;

    public UserStatusResponse(UUID id, UUID userId, Instant lastSeen, boolean isOnline) {
        this.id = id;
        this.userId = userId;
        this.lastSeen = lastSeen;
        this.isOnline = isOnline;
    }
}
