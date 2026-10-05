package com.scholartrack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Body for POST /api/attempts - one completed practice round. */
public record AttemptRequest(
        @NotNull Long gradeId,
        @NotNull Long subjectId,
        @NotNull @Min(0) Integer correct,
        @NotNull @Min(1) Integer total
) {
}
