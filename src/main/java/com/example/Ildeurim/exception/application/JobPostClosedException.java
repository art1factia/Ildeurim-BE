package com.example.Ildeurim.exception.application;

public class JobPostClosedException extends RuntimeException{
    private final Long jobPostId;

    public JobPostClosedException(Long jobPostId,String message) {
        super(message);
        this.jobPostId = jobPostId;
    }

    public Long getJobPostId(){
        return jobPostId;
    }


}
