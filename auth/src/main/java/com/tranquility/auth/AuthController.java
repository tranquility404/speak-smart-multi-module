package com.tranquility.auth;

import com.tranquility.auth.dto.AuthResponse;
import com.tranquility.auth.dto.LoginRequest;
import com.tranquility.auth.dto.RegisterRequest;
import com.tranquility.auth.service.AuthService;
import com.tranquility.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {
        String token = authService.registerWithGoogle(code);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "User registered successfully with Google!",
                        new AuthResponse(token))
                );
    }

    @PostMapping("/github/callback")
    public ResponseEntity<?> handleGithubCallback(@RequestParam String code) {
        String token = authService.registerWithGithub(code);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "User registered successfully with Github!",
                        new AuthResponse(token))
                );
    }

    @PostMapping("/linkedin/callback")
    public ResponseEntity<?> handleLinkedInCallback(@RequestParam String code) {
        String token = authService.registerWithLinkedin(code);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "User registered successfully with LinkedIn!",
                        new AuthResponse(token))
                );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String token = authService.registerWithEmail(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "User registered successfully with Email!",
                        new AuthResponse(token))
                );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Login Successful!",
                        new AuthResponse(token))
                );
    }
}