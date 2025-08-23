package com.example.Ildeurim.exception.jobPost;

public class JobPostNotFoundException extends RuntimeException {
    private final Long jobPostId;

    public JobPostNotFoundException(Long jobPostId, String message) {
        super(message);
        this.jobPostId = jobPostId;
    }

    public Long getJobPostId() {
        return jobPostId;
    }
}
