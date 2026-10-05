package com.scholartrack.controller;

import com.scholartrack.dto.AttemptRequest;
import com.scholartrack.model.Attempt;
import com.scholartrack.repository.AttemptRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/attempts")
public class AttemptController {

    private final AttemptRepository attemptRepository;

    public AttemptController(AttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    /** Records one completed practice round. This is what Progress is built from. */
    @PostMapping
    public ResponseEntity<Attempt> record(@Valid @RequestBody AttemptRequest req, HttpServletRequest request) {
        Attempt attempt = new Attempt();
        attempt.setUserId(CurrentUser.id(request));
        attempt.setGradeId(req.gradeId());
        attempt.setSubjectId(req.subjectId());
        attempt.setCorrect(req.correct());
        attempt.setTotal(req.total());
        attempt.setAttemptedAt(Instant.now());
        return ResponseEntity.ok(attemptRepository.save(attempt));
    }

    /** Wipes this account's recorded practice rounds. Used by Settings > "Reset saved progress". */
    @DeleteMapping
    public ResponseEntity<Void> resetAll(HttpServletRequest request) {
        attemptRepository.deleteAllByUserId(CurrentUser.id(request));
        return ResponseEntity.noContent().build();
    }
}
