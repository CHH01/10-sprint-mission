package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ChannelType type;

    @Column(length = 500)
    private String description;

    @ManyToMany
    @JoinTable(
        name = "channel_users",
        joinColumns = @JoinColumn(name = "channel_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private final Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ReadStatus> readStatuses = new ArrayList<>();

    public Channel(String name, ChannelType type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public Channel(String name, ChannelType type) {
        this(name, type, null);
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
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

    public void updateType(ChannelType type) {
        this.type = type;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void removeMessage(Message message) {
        messages.remove(message);
    }

    @Override
    public String toString() {
        return "채널[이름: " + name +
                ", 타입: " + type + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(getId(), channel.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
