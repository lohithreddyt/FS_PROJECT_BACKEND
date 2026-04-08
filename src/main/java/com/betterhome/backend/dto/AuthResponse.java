package com.betterhome.backend.dto;

public record AuthResponse(
        Long id,
        String name,
        String email,
        String role
) {
}
