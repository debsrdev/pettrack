package com.femcoders.pettrack.dtos.user;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role
) {
}
