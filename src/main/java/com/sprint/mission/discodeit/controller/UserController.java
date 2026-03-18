package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "User")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final BasicUserStatusService userStatusService;

  @Operation(summary = "전체 User 목록 조회", operationId = "findAll")
  @ApiResponse(responseCode = "200", description = "User 목록 조회 성공",
      content = @Content(schema = @Schema(implementation = UserDto.class)))
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<UserDto> findAll() {
    return userService.getAllUsers();
  }

  @Operation(summary = "User 등록", operationId = "create")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(examples = @ExampleObject(value = "User with email {email} already exists")))
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public UserDto createUser(
      @Parameter(description = "생성할 User 정보", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      @RequestPart("userCreateRequest") UserCreateRequest request,
      @Parameter(description = "User 프로필 이미지")
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    return userService.createUser(request, profile);
  }

  @Operation(summary = "User 정보 수정", operationId = "update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = UserDto.class))),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User with id {userId} not found"))),
      @ApiResponse(responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(examples = @ExampleObject(value = "user with email {newEmail} already exists")))
  })
  @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public UserDto updateUser(
      @Parameter(description = "수정할 User ID")
      @PathVariable UUID userId,
      @Parameter(description = "수정할 User 정보", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
      @RequestPart("userUpdateRequest") UserUpdateRequest request,
      @Parameter(description = "수정할 User 프로필 이미지")
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    return userService.updateUser(userId, request, profile);
  }

  @Operation(summary = "User 삭제", operationId = "delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User with id {id} not found")))
  })
  @DeleteMapping("/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@Parameter(description = "삭제할 User ID") @PathVariable UUID userId) {
    userService.deleteUser(userId);
  }

  @Operation(summary = "User 온라인 상태 업데이트", operationId = "updateUserStatusByUserId")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User 온라인 상태가 성공적으로 업데이트됨",
          content = @Content(schema = @Schema(implementation = UserStatusDto.class))),
      @ApiResponse(responseCode = "404", description = "해당 User의 UserStatus를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "UserStatus with userId {userId} not found")))
  })
  @PatchMapping("/{userId}/userStatus")
  @ResponseStatus(HttpStatus.OK)
  public UserStatusDto updateStatus(
      @Parameter(description = "상태를 변경할 User ID") @PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request) {
    return userStatusService.updateByUserId(userId, request);
  }

  @GetMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  public UserDto getUser(@PathVariable UUID id) {
    return userService.getUser(id);
  }
}
