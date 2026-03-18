package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @EntityGraph(attributePaths = {"author", "channel", "attachments"})
  Slice<Message> findByChannel_Id(UUID channelId, Pageable pageable);

  @EntityGraph(attributePaths = {"author", "channel", "attachments"})
  Slice<Message> findByChannel_IdAndCreatedAtBefore(UUID channelId, Instant createdAt, Pageable pageable);

  List<Message> findAllByChannel_Id(UUID channelId);

  List<Message> findAllByAuthor_Id(UUID authorId);
}
