package com.example.Ildeurim.exception.application;

public class DuplicateApplicationException extends RuntimeException {
    private final Long userId;
    private final Long jobPostId;

    public DuplicateApplicationException(Long userId, Long jobPostId, String message) {
        super(message);
        this.userId = userId;
        this.jobPostId = jobPostId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getJobPostId() {
        return jobPostId;
    }
}