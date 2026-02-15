package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;

  @PostMapping("/public")
  @Operation(summary = "Public Channel 생성")
  @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨",
      content = @Content(schema = @Schema(implementation = ChannelResponse.class)))
  @ResponseStatus(HttpStatus.CREATED)
  public ChannelResponse createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
    return channelService.createPublicChannel(request);
  }

  @PostMapping("/private")
  @Operation(summary = "Private Channel 생성")
  @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨",
      content = @Content(schema = @Schema(implementation = ChannelResponse.class)))
  @ResponseStatus(HttpStatus.CREATED)
  public ChannelResponse createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
    return channelService.createPrivateChannel(request);
  }

  @PatchMapping("/{channelId}")
  @Operation(summary = "Channel 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = ChannelDto.class))),
      @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음",
          content = @Content(examples = @ExampleObject(value = "Private channel cannot be updated"))),
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found")))
  })
  @ResponseStatus(HttpStatus.OK)
  public ChannelResponse updateChannel(
      @Parameter(description = "수정할 Channel ID", required = true)
      @PathVariable UUID channelId,
      @RequestBody ChannelUpdateRequest request) {
    return channelService.updateChannel(request);
  }

  @DeleteMapping("/{channelId}")
  @Operation(summary = "Channel 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel with id {channelId} not found")))
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteChannel(
      @Parameter(description = "삭제할 Channel ID", required = true)
      @PathVariable UUID channelId) {
    channelService.deleteChannel(channelId);
  }

  @GetMapping
  @Operation(summary = "User가 참여 중인 Channel 목록 조회")
  @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class))))
  @ResponseStatus(HttpStatus.OK)
  public List<ChannelResponse> getChannels(
      @Parameter(description = "조회할 User ID", required = true)
      @RequestParam UUID userId) {
    return channelService.findAllByUserId(userId);
  }

  @RequestMapping(value = "/all", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public List<ChannelResponse> getAllChannels() {
    return channelService.getAllChannels();
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ChannelResponse getChannel(@PathVariable UUID id) {
    return channelService.getChannel(id);
  }

  @RequestMapping(value = "/{channelId}/enter", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ChannelResponse enterChannel(@RequestParam UUID userId, @PathVariable UUID channelId) {
    return channelService.enterChannel(userId, channelId);
  }

  @RequestMapping(value = "/{channelId}/leave", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public void leaveChannel(@RequestParam UUID userId, @PathVariable UUID channelId) {
    channelService.leaveChannel(userId, channelId);
  }
}