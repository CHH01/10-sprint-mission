package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  @EntityGraph(attributePaths = {"readStatuses", "readStatuses.user", "readStatuses.user.status",
      "readStatuses.user.profile"})
  Optional<Channel> findById(UUID id);

  @Override
  @EntityGraph(attributePaths = {"readStatuses", "readStatuses.user", "readStatuses.user.status",
      "readStatuses.user.profile"})
  List<Channel> findAll();

  boolean existsByName(String name);
}
