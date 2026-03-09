package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "read_statuses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatus extends BaseUpdatableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @Column(name = "last_read_at")
  private Instant lastReadAt;

  public ReadStatus(User user, Channel channel) {
    this.user = user;
    this.channel = channel;
    this.lastReadAt = Instant.now();
  }

  public UUID getUserId() {
    return user != null ? user.getId() : null;
  }

  public UUID getChannelId() {
    return channel != null ? channel.getId() : null;
  }

  public void updateLastReadAt() {
    this.lastReadAt = Instant.now();
  }
}
