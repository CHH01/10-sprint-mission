package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import lombok.Getter;

@Getter
public class UserStatus extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID userId;
    private Instant lastSeen;

    public UserStatus(UUID userId, Instant lastSeen) {
        super();
        this.userId = userId;
        this.lastSeen = lastSeen;
    }

    public void updateLastSeen(Instant lastSeen) {
        this.lastSeen = lastSeen;
        updateTimestamps();
    }

    public boolean isOnline() {
        return lastSeen.isAfter(Instant.now().minusSeconds(300));
    }
}
