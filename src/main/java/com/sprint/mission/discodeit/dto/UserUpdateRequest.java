package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {

  private String newUsername;
  private String newEmail;
  private String newPassword;

  public UserUpdateRequest(String newUsername, String newEmail, String newPassword) {
    validate(newUsername, newEmail, newPassword);
    this.newUsername = newUsername;
    this.newEmail = newEmail;
    this.newPassword = newPassword;
  }

  private void validate(String newUsername, String newEmail, String newPassword) {
      if (newUsername != null && newUsername.isBlank()) {
          throw new IllegalArgumentException("이름은 공백일 수 없습니다.");
      }
      if (newEmail != null && !newEmail.contains("@")) {
          throw new IllegalArgumentException("이메일 형식이 아닙니다.");
      }
      if (newPassword != null && newPassword.isBlank()) {
          throw new IllegalArgumentException("비밀번호는 공백일 수 없습니다.");
      }
  }
}
