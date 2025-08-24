package com.example.Ildeurim.exception.job;

public class JobNotFoundException extends RuntimeException {
    private final Long jobId;
    public JobNotFoundException(Long jobId, String message) {

        super(message);
        this.jobId= jobId;
    }

    public Long getJobId() {
        return jobId;
    }
}
