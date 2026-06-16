package com.recovery.platform.auth.controller;

import com.recovery.platform.auth.dto.AuthResponse;
import com.recovery.platform.auth.dto.LoginRequest;
import com.recovery.platform.auth.dto.RefreshTokenRequest;
import com.recovery.platform.auth.dto.RegisterRequest;
import com.recovery.platform.auth.service.AuthService;
import com.recovery.platform.common.exception.UnauthorizedException;
import com.recovery.platform.security.annotation.CurrentUser;
import com.recovery.platform.security.userdetails.AppUserDetails;
import com.recovery.platform.user.dto.UserDto;
import com.recovery.platform.user.mapper.UserMapper;
import com.recovery.platform.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, refresh, logout, me")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @Operation(summary = "Register a new athlete / therapist / coach account")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }

    @PostMapping("/login")
    @Operation(summary = "Exchange credentials for access + refresh tokens")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rotate refresh token and issue a new access token")
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest req) {
        return authService.refresh(req.refreshToken());
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke the supplied refresh token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest req) {
        authService.logout(req.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Return the currently authenticated user")
    @PreAuthorize("isAuthenticated()")
    public UserDto me(@CurrentUser AppUserDetails me) {
        return userRepository.findById(me.getId())
                .map(userMapper::toDto)
                .orElseThrow(() -> new UnauthorizedException(
                        "ACCOUNT_NOT_FOUND", "Authenticated account no longer exists"));
    }
}
