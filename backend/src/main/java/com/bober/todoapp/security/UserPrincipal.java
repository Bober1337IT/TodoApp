package com.bober.todoapp.security;

import java.util.UUID;

public record UserPrincipal(
        UUID id,
        String email
) {
}
