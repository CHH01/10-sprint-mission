package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BasicUserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(
            @RequestPart("request") UserCreateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return userService.createUser(request, file);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUser(
            @RequestPart("request") UserUpdateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return userService.updateUser(request, file);
    }

    @RequestMapping(value = "/{userId}/status", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public UserStatusResponse updateStatus(@PathVariable UUID userId) {
        return userStatusService.updateByUserId(userId);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@RequestParam UUID id) {
        userService.deleteUser(id);
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.getAllUsers().stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.getName(),
                        user.getEmail(),
                        user.getProfileId(),
                        user.isOnline()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUser(@PathVariable UUID id) {
        return userService.getUser(id);
    }
}
