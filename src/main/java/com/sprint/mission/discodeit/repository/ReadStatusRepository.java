package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    Optional<ReadStatus> findByUser_IdAndChannel_Id(UUID userId, UUID channelId);
    List<ReadStatus> findAllByUser_Id(UUID userId);
}
