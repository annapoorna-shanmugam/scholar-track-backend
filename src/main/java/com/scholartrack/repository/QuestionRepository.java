package com.scholartrack.repository;

import com.scholartrack.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySubjectId(Long subjectId);

    long countBySubjectId(Long subjectId);

    List<Question> findBySubjectIdIn(List<Long> subjectIds);

    long countBySubjectIdIn(List<Long> subjectIds);

    Optional<Question> findByExternalKey(String externalKey);
}
