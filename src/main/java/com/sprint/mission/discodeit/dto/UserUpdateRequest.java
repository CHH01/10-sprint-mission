package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    private UUID id;
    private String name;
    private String email;

    public UserUpdateRequest(UUID id, String name, String email) {
        validate(id, name, email);
        this.id = id;
        this.name = name;
        this.email = email;
    }

    private void validate(UUID id, String name, String email) {
        if (id == null) throw new IllegalArgumentException("ID는 필수입니다.");
        if (name != null && name.isBlank()) throw new IllegalArgumentException("이름은 공백일 수 없습니다.");
        if (email != null && !email.contains("@")) throw new IllegalArgumentException("이메일 형식이 아닙니다.");
    }
}
