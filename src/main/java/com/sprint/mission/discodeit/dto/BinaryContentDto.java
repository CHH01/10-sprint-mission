package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    long size,
    String contentType,
    byte[] bytes
) {

}
