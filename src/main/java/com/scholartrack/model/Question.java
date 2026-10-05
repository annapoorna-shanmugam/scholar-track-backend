package com.scholartrack.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

/**
 * A single practice question under a subject. Content lives as JSON under
 * src/main/resources/content/ and is synced in by ContentLoader on every
 * startup - externalKey is what makes that an upsert (match on this, update
 * the rest) instead of an insert-only seed.
 */
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Stable id from the content JSON (e.g. "nursery-english-001") - how ContentLoader finds this row again to update it. */
    @Column(name = "external_key", nullable = false, unique = true)
    private String externalKey;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    /** Optional small emoji/illustration shown above the question text. */
    private String art;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    /** Stored as a native Postgres jsonb array of option strings. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<String> options;

    /** Index into `options` that is correct. */
    @Column(name = "correct_index", nullable = false)
    private Integer correctIndex;

    public Question() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalKey() {
        return externalKey;
    }

    public void setExternalKey(String externalKey) {
        this.externalKey = externalKey;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Integer getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(Integer correctIndex) {
        this.correctIndex = correctIndex;
    }
}
