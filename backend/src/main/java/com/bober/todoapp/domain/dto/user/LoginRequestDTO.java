package com.bober.todoapp.domain.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

        @NotBlank(message = ERROR_MESSAGE_EMAIL_REQUIRED)
        @Email(message = ERROR_MESSAGE_EMAIL_INVALID)
        String email,

        @NotBlank(message = ERROR_MESSAGE_PASSWORD_REQUIRED)
        String password
) {
    private static final String ERROR_MESSAGE_EMAIL_REQUIRED = "Email must not be blank";
    private static final String ERROR_MESSAGE_EMAIL_INVALID = "Email must be a valid email address";
    private static final String ERROR_MESSAGE_PASSWORD_REQUIRED = "Password must not be blank";
}
