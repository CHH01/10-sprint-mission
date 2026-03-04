package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class MessageCreateRequest {
    private UUID channelId;
    private UUID authorId;
    private String content;

    public MessageCreateRequest(UUID channelId, UUID authorId, String content) {
        validate(channelId, authorId, content);
        this.channelId = channelId;
        this.authorId = authorId;
        this.content = content;
    }

    private void validate(UUID channelId, UUID authorId, String content) {
        if (channelId == null) throw new IllegalArgumentException("채널 ID는 필수입니다.");
        if (authorId == null) throw new IllegalArgumentException("작성자 ID는 필수입니다.");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("메시지 내용은 필수입니다.");
    }
}
