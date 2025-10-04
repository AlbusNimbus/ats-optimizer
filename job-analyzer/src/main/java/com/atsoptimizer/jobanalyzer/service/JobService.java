package com.atsoptimizer.jobanalyzer.service;

import com.atsoptimizer.jobanalyzer.dto.JobCreateRequest;
import com.atsoptimizer.jobanalyzer.dto.JobResponse;
import com.atsoptimizer.jobanalyzer.exception.JobNotFoundException;
import com.atsoptimizer.jobanalyzer.model.Job;
import com.atsoptimizer.jobanalyzer.repository.JobRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final KeywordExtractionService keywordExtractionService;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    /**
     * Create a new job posting
     */
    @Transactional
    public JobResponse createJob(JobCreateRequest request) {
        log.info("Creating job for user: {}, title: {}", request.getUserId(), request.getTitle());

        // Combine description and requirements for analysis
        String fullText = request.getDescription() + " " +
                (request.getRequirements() != null ? request.getRequirements() : "");

        // Extract information using KeywordExtractionService
        List<String> keywords = keywordExtractionService.extractKeywords(fullText);
        List<String> requiredSkills = keywordExtractionService.extractRequiredSkills(fullText);
        List<String> preferredSkills = keywordExtractionService.extractPreferredSkills(fullText);
        String experienceLevel = keywordExtractionService.detectExperienceLevel(fullText);
        String educationLevel = keywordExtractionService.detectEducationLevel(fullText);

        // Build job entity
        Job job = Job.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .company(request.getCompany())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .extractedKeywords(toJson(keywords))
                .requiredSkills(toJson(requiredSkills))
                .preferredSkills(toJson(preferredSkills))
                .experienceLevel(experienceLevel)
                .educationLevel(educationLevel)
                .location(request.getLocation())
                .jobType(request.getJobType())
                .sourceUrl(request.getSourceUrl())
                .build();

        Job savedJob = jobRepository.save(job);
        log.info("Job created successfully with id: {}", savedJob.getId());

        // Invalidate user's jobs cache
        cacheService.invalidateUserJobs(request.getUserId());

        return toJobResponse(savedJob);
    }

    /**
     * Get job by ID (with caching)
     */
    public JobResponse getJob(Long jobId) {
        log.debug("Fetching job with id: {}", jobId);

        // Check cache first
        JobResponse cachedJob = cacheService.getCachedJob(jobId);
        if (cachedJob != null) {
            return cachedJob;
        }

        // Fetch from database
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));

        JobResponse response = toJobResponse(job);

        // Cache the response
        cacheService.cacheJob(jobId, response);

        return response;
    }

    /**
     * Get all jobs for a user
     */
    public List<JobResponse> getUserJobs(String userId) {
        log.debug("Fetching jobs for user: {}", userId);

        List<Job> jobs = jobRepository.findByUserId(userId);
        return jobs.stream()
                .map(this::toJobResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search jobs by keyword
     */
    public List<JobResponse> searchJobs(String keyword) {
        log.debug("Searching jobs with keyword: {}", keyword);

        List<Job> jobs = jobRepository.searchByKeyword(keyword);
        return jobs.stream()
                .map(this::toJobResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update job
     */
    @Transactional
    public JobResponse updateJob(Long jobId, JobCreateRequest request) {
        log.info("Updating job with id: {}", jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));

        // Update fields
        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setSourceUrl(request.getSourceUrl());

        // Re-extract information
        String fullText = request.getDescription() + " " +
                (request.getRequirements() != null ? request.getRequirements() : "");

        List<String> keywords = keywordExtractionService.extractKeywords(fullText);
        List<String> requiredSkills = keywordExtractionService.extractRequiredSkills(fullText);
        List<String> preferredSkills = keywordExtractionService.extractPreferredSkills(fullText);
        String experienceLevel = keywordExtractionService.detectExperienceLevel(fullText);
        String educationLevel = keywordExtractionService.detectEducationLevel(fullText);

        job.setExtractedKeywords(toJson(keywords));
        job.setRequiredSkills(toJson(requiredSkills));
        job.setPreferredSkills(toJson(preferredSkills));
        job.setExperienceLevel(experienceLevel);
        job.setEducationLevel(educationLevel);

        Job updatedJob = jobRepository.save(job);

        // Invalidate cache
        cacheService.invalidateJob(jobId);
        cacheService.invalidateUserJobs(job.getUserId());

        return toJobResponse(updatedJob);
    }

    /**
     * Delete job
     */
    @Transactional
    public void deleteJob(Long jobId) {
        log.info("Deleting job with id: {}", jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));

        jobRepository.delete(job);

        // Invalidate cache
        cacheService.invalidateJob(jobId);
        cacheService.invalidateUserJobs(job.getUserId());
    }

    /**
     * Convert Job entity to JobResponse DTO
     */
    private JobResponse toJobResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .userId(job.getUserId())
                .title(job.getTitle())
                .company(job.getCompany())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .extractedKeywords(fromJson(job.getExtractedKeywords()))
                .requiredSkills(fromJson(job.getRequiredSkills()))
                .preferredSkills(fromJson(job.getPreferredSkills()))
                .experienceLevel(job.getExperienceLevel())
                .educationLevel(job.getEducationLevel())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .sourceUrl(job.getSourceUrl())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    /**
     * Convert list to JSON string
     */
    private String toJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.error("Error converting list to JSON", e);
            return "[]";
        }
    }

    /**
     * Convert JSON string to list
     */
    private List<String> fromJson(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to list", e);
            return Collections.emptyList();
        }
    }
}