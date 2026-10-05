package com.scholartrack.dto;

import java.util.List;

/** Everything the Progress screen needs, precomputed server-side. */
public record ProgressResponse(
        int streakDays,
        int totalQuestions,
        int accuracyPercent,
        List<SubjectProgress> bySubject,
        List<ActivityItem> recentActivity,
        List<Badge> badges
) {
    public record SubjectProgress(
            Long subjectId, String subjectKey, String subjectName,
            String icon, String color, int correct, int total, int percent
    ) {
    }

    public record ActivityItem(
            String subjectKey, String subjectName, String icon, String color,
            String gradeName, int correct, int total, String attemptedAt
    ) {
    }

    public record Badge(String icon, String name, boolean earned) {
    }
}
