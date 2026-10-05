package com.scholartrack.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholartrack.model.Grade;
import com.scholartrack.model.Question;
import com.scholartrack.model.Subject;
import com.scholartrack.repository.GradeRepository;
import com.scholartrack.repository.QuestionRepository;
import com.scholartrack.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Syncs grades/subjects/questions from src/main/resources/content/**\/*.json
 * into the database on every startup. Each level has a stable key
 * (grade.key, subject.gradeId+key, question.externalKey) so this is a real
 * upsert - editing a JSON file and restarting updates the matching row
 * in place. Growing the catalog (more grades, more subjects, more
 * questions) is purely a matter of adding/editing these JSON files; no
 * Java code changes needed.
 *
 * Content removed from a JSON file is left in the database rather than
 * deleted - that needs a deliberate manual step, not an accidental typo.
 */
@Component
public class ContentLoader implements CommandLineRunner {

    private static final String GRADES_FILE = "content/grades.json";
    private static final String SUBJECT_FILE_PATTERN = "classpath:content/%s/*.json";

    private final GradeRepository gradeRepository;
    private final SubjectRepository subjectRepository;
    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper;

    public ContentLoader(GradeRepository gradeRepository, SubjectRepository subjectRepository,
                          QuestionRepository questionRepository, ObjectMapper objectMapper) {
        this.gradeRepository = gradeRepository;
        this.subjectRepository = subjectRepository;
        this.questionRepository = questionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws IOException {
        List<GradeContent> grades = objectMapper.readValue(
                new org.springframework.core.io.ClassPathResource(GRADES_FILE).getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, GradeContent.class));

        for (GradeContent gradeContent : grades) {
            Grade grade = upsertGrade(gradeContent);
            for (Resource subjectFile : subjectFiles(gradeContent.key())) {
                SubjectContent subjectContent = objectMapper.readValue(subjectFile.getInputStream(), SubjectContent.class);
                Subject subject = upsertSubject(grade.getId(), subjectContent);
                for (QuestionContent questionContent : subjectContent.questions()) {
                    upsertQuestion(subject.getId(), questionContent);
                }
            }
        }
    }

    private Resource[] subjectFiles(String gradeKey) throws IOException {
        return new PathMatchingResourcePatternResolver().getResources(String.format(SUBJECT_FILE_PATTERN, gradeKey));
    }

    private Grade upsertGrade(GradeContent content) {
        Grade grade = gradeRepository.findByKey(content.key()).orElseGet(Grade::new);
        grade.setKey(content.key());
        grade.setName(content.name());
        grade.setAgeRange(content.ageRange());
        return gradeRepository.save(grade);
    }

    private Subject upsertSubject(Long gradeId, SubjectContent content) {
        Subject subject = subjectRepository.findByGradeIdAndKey(gradeId, content.key()).orElseGet(Subject::new);
        subject.setGradeId(gradeId);
        subject.setKey(content.key());
        subject.setName(content.name());
        subject.setIcon(content.icon());
        subject.setColor(content.color());
        subject.setDescription(content.description());
        return subjectRepository.save(subject);
    }

    private void upsertQuestion(Long subjectId, QuestionContent content) {
        Question question = questionRepository.findByExternalKey(content.key()).orElseGet(Question::new);
        question.setExternalKey(content.key());
        question.setSubjectId(subjectId);
        question.setArt(content.art());
        question.setQuestionText(content.text());
        question.setOptions(content.options());
        question.setCorrectIndex(content.correctIndex());
        questionRepository.save(question);
    }

    private record GradeContent(String key, String name, String ageRange) {
    }

    private record SubjectContent(String key, String name, String icon, String color, String description,
                                   List<QuestionContent> questions) {
    }

    private record QuestionContent(String key, String art, String text, List<String> options, Integer correctIndex) {
    }
}
