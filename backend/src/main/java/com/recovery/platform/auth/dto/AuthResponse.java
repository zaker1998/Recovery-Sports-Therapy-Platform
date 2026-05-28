package com.recovery.platform.auth.dto;

import com.recovery.platform.user.dto.UserDto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        UserDto user
) {
    public static AuthResponse of(String access, String refresh, long ttl, UserDto user) {
        return new AuthResponse(access, refresh, "Bearer", ttl, user);
    }
}
