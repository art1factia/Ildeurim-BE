package com.example.Ildeurim.exception.job;

public class JobPermissionException extends RuntimeException {
    private final Long jobId;

    public JobPermissionException(Long jobId,String message) {

        super(message);
        this.jobId=jobId;
    }

    public Long getJobId() {
        return jobId;
    }
}
