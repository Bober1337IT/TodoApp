package com.bober.todoapp.domain.dto.user;

public record AuthResponseDTO(
        String accessToken,
        String tokenType,
        String email
) {
    public static AuthResponseDTO bearer(String accessToken, String email) {
        return new AuthResponseDTO(accessToken, "Bearer", email);
    }
}
