package com.scholartrack.model;

import jakarta.persistence.*;

/**
 * A grade / class level (e.g. Nursery, LKG, Class 1...). More grades get added
 * later just as new rows here - no code change needed.
 */
@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Stable machine key, e.g. "nursery". Never shown to the user directly. */
    @Column(name = "grade_key", nullable = false, unique = true)
    private String key;

    /** Display name, e.g. "Nursery". */
    @Column(nullable = false)
    private String name;

    /** Display age range, e.g. "Age 3-4". */
    @Column(name = "age_range")
    private String ageRange;

    public Grade() {
    }

    public Grade(String key, String name, String ageRange) {
        this.key = key;
        this.name = name;
        this.ageRange = ageRange;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }
}
