package com.bober.todoapp.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record RegisterRequestDTO(

        @NotBlank(message = ERROR_MESSAGE_EMAIL_REQUIRED)
        @Email(message = ERROR_MESSAGE_EMAIL_INVALID)
        String email,

        @NotBlank(message = ERROR_MESSAGE_PASSWORD_REQUIRED)
        @Length(min = 8, max = 64, message = ERROR_MESSAGE_PASSWORD_LENGTH)
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
                message = ERROR_MESSAGE_PASSWORD_PATTERN
        )
        String password
) {
    private static final String ERROR_MESSAGE_EMAIL_REQUIRED = "Email must not be blank";
    private static final String ERROR_MESSAGE_EMAIL_INVALID = "Email must be a valid email address";
    private static final String ERROR_MESSAGE_PASSWORD_REQUIRED = "Password must not be blank";
    private static final String ERROR_MESSAGE_PASSWORD_LENGTH = "Password must be between 8 and 64 characters";
    private static final String ERROR_MESSAGE_PASSWORD_PATTERN = "Password must contain uppercase, lowercase, digit and special character (@$!%*?&)";
}
