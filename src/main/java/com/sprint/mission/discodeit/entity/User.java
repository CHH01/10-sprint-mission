package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

  @Column(name = "username", nullable = false, length = 50, unique = true)
  private String name;

  @Column(nullable = false, length = 100, unique = true)
  private String email;

  @Column(nullable = false, length = 60)
  private String password;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "profile_id", unique = true)
  private BinaryContent profile;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private UserStatus status;

  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<Message> messages = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<ReadStatus> readStatuses = new ArrayList<>();

  public User(String name, String email, String password, BinaryContent profile) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.profile = profile;
  }

  public User(String name, String email, BinaryContent profile) {
    this(name, email, null, profile);
  }

  public User(String name, String email) {
    this(name, email, null, null);
  }

  public void updateStatus(UserStatus status) {
    this.status = status;
  }

  public void addMessage(Message message) {
    messages.add(message);
  }

  public void addReadStatus(ReadStatus readStatus) {
    readStatuses.add(readStatus);
  }

  public void removeReadStatus(ReadStatus readStatus) {
    readStatuses.remove(readStatus);
  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updateEmail(String email) {
    this.email = email;
  }

  public void updatePassword(String password) {
    this.password = password;
  }

  public UUID getProfileId() {
    return profile != null ? profile.getId() : null;
  }

  public void updateProfile(BinaryContent profile) {
    this.profile = profile;
  }

  public void updateProfileId(BinaryContent profile) {
    this.profile = profile;
  }

  public void removeMessage(Message message) {
    this.messages.remove(message);
  }

  @Override
  public String toString() {
    return "유저[이름: " + name +
        ", 이메일: " + email + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(getId(), user.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
