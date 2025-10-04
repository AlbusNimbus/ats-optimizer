package com.atsoptimizer.jobanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    private Long id;
    private String userId;
    private String title;
    private String company;
    private String description;
    private String requirements;
    private List<String> extractedKeywords;
    private List<String> requiredSkills;
    private List<String> preferredSkills;
    private String experienceLevel;
    private String educationLevel;
    private String location;
    private String jobType;
    private String sourceUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}