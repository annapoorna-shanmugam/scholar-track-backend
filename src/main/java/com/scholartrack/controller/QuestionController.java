package com.scholartrack.controller;

import com.scholartrack.model.Question;
import com.scholartrack.model.Subject;
import com.scholartrack.repository.QuestionRepository;
import com.scholartrack.repository.SubjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subjects/{subjectId}/questions")
public class QuestionController {

    /** Stable machine key for the synthetic "mix of every other subject in this grade" subject - see DataSeeder. */
    private static final String COMBINED_KEY = "combined";

    private final QuestionRepository questionRepository;
    private final SubjectRepository subjectRepository;

    public QuestionController(QuestionRepository questionRepository, SubjectRepository subjectRepository) {
        this.questionRepository = questionRepository;
        this.subjectRepository = subjectRepository;
    }

    /**
     * Returns up to `limit` questions for a subject (all of them if limit is
     * omitted or bigger than what exists). For the "combined" subject, this
     * instead mixes questions from every other subject in the same grade,
     * round-robin, so one subject's questions don't all land up front.
     */
    @GetMapping
    public ResponseEntity<List<Question>> questionsForSubject(
            @PathVariable Long subjectId,
            @RequestParam(required = false) Integer limit) {
        Subject subject = subjectRepository.findById(subjectId).orElse(null);
        if (subject == null) {
            return ResponseEntity.notFound().build();
        }

        List<Question> questions = COMBINED_KEY.equals(subject.getKey())
                ? interleaved(combinedSiblingIds(subject))
                : questionRepository.findBySubjectId(subjectId);

        if (limit != null && limit >= 0 && limit < questions.size()) {
            questions = questions.subList(0, limit);
        }
        return ResponseEntity.ok(questions);
    }

    /** Cheap existence/count check the frontend uses to show "N sample Qs" vs "Coming soon". */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> count(@PathVariable Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId).orElse(null);
        if (subject == null) {
            return ResponseEntity.notFound().build();
        }

        long count = COMBINED_KEY.equals(subject.getKey())
                ? questionRepository.countBySubjectIdIn(combinedSiblingIds(subject))
                : questionRepository.countBySubjectId(subjectId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    private List<Long> combinedSiblingIds(Subject combined) {
        return subjectRepository.findByGradeId(combined.getGradeId()).stream()
                .filter(s -> !COMBINED_KEY.equals(s.getKey()))
                .map(Subject::getId)
                .toList();
    }

    /** Round-robins across each subject's own question order, so a combined session mixes topics instead of running them back to back. */
    private List<Question> interleaved(List<Long> subjectIds) {
        Map<Long, List<Question>> bySubject = new LinkedHashMap<>();
        for (Long id : subjectIds) {
            bySubject.put(id, questionRepository.findBySubjectId(id));
        }

        List<Question> result = new ArrayList<>();
        int index = 0;
        boolean any = true;
        while (any) {
            any = false;
            for (List<Question> subjectQuestions : bySubject.values()) {
                if (index < subjectQuestions.size()) {
                    result.add(subjectQuestions.get(index));
                    any = true;
                }
            }
            index++;
        }
        return result;
    }
}
