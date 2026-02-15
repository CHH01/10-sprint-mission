package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Message 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Message가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = MessageDto.class))),
      @ApiResponse(responseCode = "404", description = "Channel 또는 User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Channel | Author with id {channelId | authorId} not found")))
  })
  @ResponseStatus(HttpStatus.CREATED)
  public MessageResponse createMessage(
      @Parameter(description = "Message 생성 정보", required = true)
      @RequestPart("request") MessageCreateRequest request,
      @Parameter(description = "Message 첨부 파일들")
      @RequestPart(value = "files", required = false) List<MultipartFile> files) {
    return messageService.createMessage(request, files);
  }

  @GetMapping
  @Operation(summary = "Channel의 Message 목록 조회")
  @ApiResponse(responseCode = "200", description = "Message 목록 조회 성공",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = MessageDto.class))))
  @ResponseStatus(HttpStatus.OK)
  public List<MessageResponse> getMessages(
      @Parameter(description = "조회할 Channel ID", required = true)
      @RequestParam UUID channelId) {
    return messageService.findAllByChannelId(channelId);
  }

  @RequestMapping(value = "/all", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public List<MessageResponse> getAllMessages() {
    return messageService.getAllMessages();
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public MessageResponse getMessage(@PathVariable UUID id) {
    return messageService.getMessage(id);
  }

  @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public List<MessageResponse> getMessagesByUser(@PathVariable UUID userId) {
    return messageService.getMessagesByUserId(userId);
  }

  @PatchMapping(value = "/{messageId}")
  @Operation(summary = "Message 내용 수정")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Message가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = MessageDto.class))),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found")))
  })
  @ResponseStatus(HttpStatus.OK)
  public MessageResponse updateMessage(
      @Parameter(description = "수정할 Message ID", required = true)
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest request) {
    return messageService.updateMessage(request, null);
  }

  @DeleteMapping("/{messageId}")
  @Operation(summary = "Message 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Message가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "404", description = "Message를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Message with id {messageId} not found")))
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteMessage(
      @Parameter(description = "삭제할 Message ID", required = true)
      @RequestParam UUID id) {
    messageService.deleteMessage(id);
  }
}