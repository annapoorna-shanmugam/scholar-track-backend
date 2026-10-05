package com.scholartrack.controller;

import com.scholartrack.model.Subject;
import com.scholartrack.repository.GradeRepository;
import com.scholartrack.repository.SubjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/grades/{gradeId}/subjects")
public class SubjectController {

    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;

    public SubjectController(SubjectRepository subjectRepository, GradeRepository gradeRepository) {
        this.subjectRepository = subjectRepository;
        this.gradeRepository = gradeRepository;
    }

    @GetMapping
    public ResponseEntity<List<Subject>> subjectsForGrade(@PathVariable Long gradeId) {
        if (!gradeRepository.existsById(gradeId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(subjectRepository.findByGradeId(gradeId));
    }
}
