package com.tranquility.user.controller;

import com.tranquility.common.dto.ApiResponse;
import com.tranquility.common.file.FileReadException;
import com.tranquility.user.dto.UpdateUserProfileRequest;
import com.tranquility.user.dto.UserOverview;
import com.tranquility.common.user.model.UserPrincipal;
import com.tranquility.user.service.UserService;
import com.tranquility.user.validation.ValidProfilePicture;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@Parameter(hidden = true) UserPrincipal currentUser) {
        UserOverview user = userService.getUser(currentUser.getId());

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Fetched User Details!",
                        user
                ));
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateMyInfo(
            @Parameter(hidden = true) UserPrincipal currentUser,
            @RequestBody UpdateUserProfileRequest request
    ) {
        userService.updateUser(currentUser.getId(), request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "User Profile Updated!",
                        null
                ));
    }

    @GetMapping
    public ResponseEntity<?> getLeaderboard(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(),
                        "Fetched Leaderboard!",
                        userService.getLeaderboard(cursor, size)
                ));
    }

    @GetMapping("/me/profile-picture")
    public ResponseEntity<?> getProfilePicture(@Parameter(hidden = true) UserPrincipal currentUser) {
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(),
                        "Fetched Profile Picture!",
                        userService.getProfilePicUrl(currentUser.getId())
                ));
    }

    @PostMapping(
            value = "/me/profile-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> updateProfilePicture(
            @Parameter(hidden = true) UserPrincipal currentUser,
            @ValidProfilePicture @RequestParam("file") MultipartFile file
    ) {
        try {
            return ResponseEntity.ok(
                    ApiResponse.success(HttpStatus.OK.value(),
                            "Profile Picture Updated!",
                            userService.updateProfilePic(currentUser.getId(), file)
                ));
        } catch (IOException e) {
            throw new FileReadException("Failed to read file!");
        }
    }
}
