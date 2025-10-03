package com.atsoptimizer.jobanalyzer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobCreateRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Job title is required")
    private String title;

    private String company;

    @NotBlank(message = "Job description is required")
    private String description;

    private String requirements;

    private String location;

    private String jobType;

    private String sourceUrl;
}