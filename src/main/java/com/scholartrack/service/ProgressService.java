package com.scholartrack.service;

import com.scholartrack.dto.ProgressResponse;
import com.scholartrack.model.Attempt;
import com.scholartrack.model.Grade;
import com.scholartrack.model.Subject;
import com.scholartrack.repository.AttemptRepository;
import com.scholartrack.repository.GradeRepository;
import com.scholartrack.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Turns raw Attempt rows into everything the Progress screen shows. There is
 * no stored "progress" - it is always recomputed from attempts, so it can
 * never drift out of sync with what actually happened in practice.
 */
@Service
public class ProgressService {

    private final AttemptRepository attemptRepository;
    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;

    // Local, not UTC: a practice round after 11pm and one just after midnight
    // should usually still count as the same "day" from a parent's point of view.
    private static final ZoneId ZONE = ZoneId.systemDefault();

    public ProgressService(AttemptRepository attemptRepository,
                            SubjectRepository subjectRepository,
                            GradeRepository gradeRepository) {
        this.attemptRepository = attemptRepository;
        this.subjectRepository = subjectRepository;
        this.gradeRepository = gradeRepository;
    }

    public ProgressResponse build(Long userId) {
        List<Attempt> attempts = attemptRepository.findAllByUserIdOrderByAttemptedAtDesc(userId);

        Map<Long, Subject> subjectsById = subjectRepository.findAll().stream()
                .collect(Collectors.toMap(Subject::getId, s -> s));
        Map<Long, Grade> gradesById = gradeRepository.findAll().stream()
                .collect(Collectors.toMap(Grade::getId, g -> g));

        int totalCorrect = 0;
        int totalQuestions = 0;
        Set<LocalDate> daysPracticed = new HashSet<>();
        Map<Long, int[]> bySubjectId = new LinkedHashMap<>(); // subjectId -> [correct, total]

        for (Attempt a : attempts) {
            totalCorrect += a.getCorrect();
            totalQuestions += a.getTotal();
            daysPracticed.add(LocalDate.ofInstant(a.getAttemptedAt(), ZONE));
            int[] agg = bySubjectId.computeIfAbsent(a.getSubjectId(), k -> new int[2]);
            agg[0] += a.getCorrect();
            agg[1] += a.getTotal();
        }

        int accuracyPercent = totalQuestions == 0 ? 0 : Math.round(100f * totalCorrect / totalQuestions);
        int streakDays = daysPracticed.size();

        List<ProgressResponse.SubjectProgress> bySubject = bySubjectId.entrySet().stream()
                .map(e -> {
                    Subject s = subjectsById.get(e.getKey());
                    int correct = e.getValue()[0];
                    int total = e.getValue()[1];
                    int percent = total == 0 ? 0 : Math.round(100f * correct / total);
                    return new ProgressResponse.SubjectProgress(
                            e.getKey(),
                            s != null ? s.getKey() : "unknown",
                            s != null ? s.getName() : "Unknown subject",
                            s != null ? s.getIcon() : "❓",
                            s != null ? s.getColor() : "var(--primary)",
                            correct, total, percent
                    );
                })
                .collect(Collectors.toList());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d MMM, h:mm a");
        List<ProgressResponse.ActivityItem> recentActivity = attempts.stream()
                .limit(6)
                .map(a -> {
                    Subject s = subjectsById.get(a.getSubjectId());
                    Grade g = gradesById.get(a.getGradeId());
                    return new ProgressResponse.ActivityItem(
                            s != null ? s.getKey() : "unknown",
                            s != null ? s.getName() : "Unknown subject",
                            s != null ? s.getIcon() : "❓",
                            s != null ? s.getColor() : "var(--primary)",
                            g != null ? g.getName() : "",
                            a.getCorrect(), a.getTotal(),
                            fmt.format(a.getAttemptedAt().atZone(ZONE))
                    );
                })
                .collect(Collectors.toList());

        boolean anyPerfect = attempts.stream().anyMatch(a -> a.getCorrect().equals(a.getTotal()));
        long totalSubjectsOffered = subjectRepository.count();
        boolean triedEverySubject = totalSubjectsOffered > 0 && bySubjectId.size() >= totalSubjectsOffered;

        List<ProgressResponse.Badge> badges = List.of(
                new ProgressResponse.Badge("🔥", "3-Day Streak", streakDays >= 3),
                new ProgressResponse.Badge("🌟", "Perfect Score", anyPerfect),
                new ProgressResponse.Badge("🎯", "Tried Every Subject", triedEverySubject)
        );

        return new ProgressResponse(streakDays, totalQuestions, accuracyPercent, bySubject, recentActivity, badges);
    }
}
