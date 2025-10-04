package com.atsoptimizer.jobanalyzer.controller;

import com.atsoptimizer.jobanalyzer.dto.JobCreateRequest;
import com.atsoptimizer.jobanalyzer.dto.JobResponse;
import com.atsoptimizer.jobanalyzer.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    /**
     * Create a new job posting
     */
    @PostMapping
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobCreateRequest request) {
        log.info("Received request to create job: {}", request.getTitle());
        JobResponse response = jobService.createJob(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get job by ID
     */
    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJob(@PathVariable Long jobId) {
        log.info("Received request to get job: {}", jobId);
        JobResponse response = jobService.getJob(jobId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all jobs for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<JobResponse>> getUserJobs(@PathVariable String userId) {
        log.info("Received request to get jobs for user: {}", userId);
        List<JobResponse> response = jobService.getUserJobs(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Search jobs by keyword
     */
    @GetMapping("/search")
    public ResponseEntity<List<JobResponse>> searchJobs(@RequestParam String keyword) {
        log.info("Received search request with keyword: {}", keyword);
        List<JobResponse> response = jobService.searchJobs(keyword);
        return ResponseEntity.ok(response);
    }

    /**
     * Update job
     */
    @PutMapping("/{jobId}")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long jobId,
            @Valid @RequestBody JobCreateRequest request) {
        log.info("Received request to update job: {}", jobId);
        JobResponse response = jobService.updateJob(jobId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete job
     */
    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        log.info("Received request to delete job: {}", jobId);
        jobService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "job-analyzer"
        ));
    }
}