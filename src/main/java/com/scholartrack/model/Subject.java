package com.scholartrack.model;

import jakarta.persistence.*;

/**
 * A subject offered within a grade (e.g. English Olympiad for Nursery).
 * Deliberately holds gradeId as a plain foreign key rather than a JPA
 * relationship, so JSON serialization stays simple with no cycles to manage.
 */
@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grade_id", nullable = false)
    private Long gradeId;

    /** Stable machine key, e.g. "english". */
    @Column(name = "subject_key", nullable = false)
    private String key;

    /** Display name, e.g. "English Olympiad". */
    @Column(nullable = false)
    private String name;

    /** A single emoji used as the subject's icon in the UI. */
    private String icon;

    /** CSS color value/token the frontend uses to tint this subject's cards. */
    private String color;

    private String description;

    public Subject() {
    }

    public Subject(Long gradeId, String key, String name, String icon, String color, String description) {
        this.gradeId = gradeId;
        this.key = key;
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
