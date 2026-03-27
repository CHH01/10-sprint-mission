package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "Channel")
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;

  @Operation(summary = "Public Channel 생성", operationId = "create_3")
  @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨",
      content = @Content(schema = @Schema(implementation = ChannelDto.class)))
  @PostMapping("/public")
  @ResponseStatus(HttpStatus.CREATED)
  public ChannelDto createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
    log.info("REST request to create Public Channel: {}", request.getName());
    return channelService.createPublicChannel(request);
  }

  @Operation(summary = "Private Channel 생성", operationId = "create_4")
  @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
      content = @Content(schema = @Schema(implementation = ChannelDto.class)))
  @PostMapping("/private")
  @ResponseStatus(HttpStatus.CREATED)
  public ChannelDto createPrivateChannel(@RequestBody @Valid PrivateChannelCreateRequest request) {
    log.info("REST request to create Private Channel");
    return channelService.createPrivateChannel(request);
  }

  @Operation(summary = "Channel 정보 수정", operationId = "update_3")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = ChannelDto.class))),
      @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음",
          content = @Content(examples = @ExampleObject(value = "Private channel cannot be updated"))),
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found")))
  })
  @PatchMapping("/{channelId}")
  @ResponseStatus(HttpStatus.OK)
  public ChannelDto updateChannel(
      @Parameter(description = "수정할 Channel ID", required = true)
      @PathVariable UUID channelId,
      @RequestBody @Valid PublicChannelUpdateRequest request) {
    log.info("REST request to update Channel: id={}", channelId);
    return channelService.updateChannel(channelId, request);
  }

  @Operation(summary = "Channel 삭제", operationId = "delete_2")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found")))
  })
  @DeleteMapping("/{channelId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteChannel(
      @Parameter(description = "삭제할 Channel ID", required = true)
      @PathVariable UUID channelId) {
    log.info("REST request to delete Channel: id={}", channelId);
    channelService.deleteChannel(channelId);
  }

  @Operation(summary = "User가 참여 중인 Channel 목록 조회", operationId = "findAll_1")
  @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공",
      content = @Content(schema = @Schema(implementation = ChannelDto.class)))
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<ChannelDto> getChannels(
      @Parameter(description = "조회할 User ID", required = true)
      @RequestParam UUID userId) {
    log.debug("REST request to get Channels for user: id={}", userId);
    return channelService.findAllByUserId(userId);
  }

  @GetMapping("/all")
  @ResponseStatus(HttpStatus.OK)
  public List<ChannelDto> getAllChannels() {
    log.debug("REST request to get all Channels");
    return channelService.getAllChannels();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ChannelDto getChannel(@PathVariable UUID id) {
    log.debug("REST request to get Channel: id={}", id);
    return channelService.getChannel(id);
  }

  @Operation(summary = "Channel 입장", operationId = "enter_1")
  @PostMapping("/{channelId}/members")
  @ResponseStatus(HttpStatus.OK)
  public ChannelDto enterChannel(
      @Parameter(description = "입장할 User ID", required = true)
      @RequestParam UUID userId,
      @PathVariable UUID channelId) {
    log.info("REST request to enter Channel: userId={}, channelId={}", userId, channelId);
    return channelService.enterChannel(userId, channelId);
  }

  @Operation(summary = "Channel 퇴장", operationId = "leave_1")
  @DeleteMapping("/{channelId}/members/{userId}")
  @ResponseStatus(HttpStatus.OK)
  public void leaveChannel(
      @Parameter(description = "퇴장할 User ID", required = true)
      @PathVariable UUID userId,
      @PathVariable UUID channelId) {
    log.info("REST request to leave Channel: userId={}, channelId={}", userId, channelId);
    channelService.leaveChannel(userId, channelId);
  }
}
