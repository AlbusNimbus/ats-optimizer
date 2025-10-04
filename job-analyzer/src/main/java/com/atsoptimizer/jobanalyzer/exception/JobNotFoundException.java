package com.atsoptimizer.jobanalyzer.exception;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(String message) {
        super(message);
    }

    public JobNotFoundException(Long id) {
        super("Job not found with id: " + id);
    }
}