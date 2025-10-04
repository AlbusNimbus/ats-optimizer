package com.atsoptimizer.jobanalyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs", indexes = {
        @Index(name = "idx_user_id", columnList = "userId"),
        @Index(name = "idx_title", columnList = "title")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 500)
    private String company;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String extractedKeywords;  // JSON array

    @Column(columnDefinition = "TEXT")
    private String requiredSkills;  // JSON array

    @Column(columnDefinition = "TEXT")
    private String preferredSkills;  // JSON array

    @Column
    private String experienceLevel;  // Entry, Mid, Senior, etc.

    @Column
    private String educationLevel;  // Bachelor's, Master's, etc.

    @Column(length = 1000)
    private String location;

    @Column
    private String jobType;  // Full-time, Part-time, Contract, etc.

    @Column
    private String sourceUrl;  // If scraped from URL

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}