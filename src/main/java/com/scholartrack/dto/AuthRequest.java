package com.scholartrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Body for POST /api/auth/register and POST /api/auth/login. Password rule:
 * 4-12 characters, any mix of letters/digits/special characters, no
 * whitespace.
 */
public record AuthRequest(
        @NotBlank @Size(min = 3, max = 24) @Pattern(regexp = "^[A-Za-z0-9_.-]+$", message = "use only letters, numbers, . _ -") String username,
        @NotBlank @Size(min = 4, max = 12) @Pattern(regexp = "^\\S+$", message = "no spaces allowed") String password
) {
}
