package com.nova.membership.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

/**
 * "identifier" = email OR mobile number (the login form says "Mobile / Email").
 * The old JSON key "email" is still accepted through @JsonAlias.
 */
public record LoginRequest(
        @NotBlank(message = "Enter your email or mobile number.")
        @JsonAlias("email")
        String identifier,

        @NotBlank(message = "Enter your password.")
        String password) {
}
