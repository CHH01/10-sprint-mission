package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserCreateRequest {
  private String username;
  private String email;
  private String password;

  public UserCreateRequest(String username, String email, String password) {
    validate(username, email, password);
    this.username = username;
    this.email = email;
    this.password = password;
  }

  private void validate(String username, String email, String password) {
      if (username == null || username.isBlank()) {
          throw new IllegalArgumentException("이름은 필수입니다.");
      }
      if (email == null || email.isBlank()) {
          throw new IllegalArgumentException("이메일은 필수입니다.");
      }
      if (!email.contains("@")) {
          throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
      }
      if (password == null || password.isBlank()) {
          throw new IllegalArgumentException("비밀번호는 필수입니다.");
      }
  }
}
