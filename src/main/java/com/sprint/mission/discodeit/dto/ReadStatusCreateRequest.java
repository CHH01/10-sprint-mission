package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ReadStatusCreateRequest {
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    public ReadStatusCreateRequest(UUID userId, UUID channelId, Instant lastReadAt) {
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public ReadStatusCreateRequest(UUID userId, UUID channelId) {
        this(userId, channelId, null);
    }
}
