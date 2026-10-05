package com.scholartrack.dto;

/** What the frontend gets back for the signed-in account - never the password hash. */
public record UserResponse(Long id, String username) {
}
