package com.example.Ildeurim.exception.jobPost;

public class JobPostPermissionException extends RuntimeException {
    private final Long jobPostId;
    public JobPostPermissionException(Long jobPostId,String message) {

        super(message);
        this.jobPostId = jobPostId;
    }

    public Long getJobPostId() {
        return jobPostId;
    }
}
