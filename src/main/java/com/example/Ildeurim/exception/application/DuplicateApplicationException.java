package com.example.Ildeurim.exception.application;

public class DuplicateApplicationException extends RuntimeException {
    private final Long workerId;
    private final Long jobPostId;

    public DuplicateApplicationException(Long workerId, Long jobPostId) {
        super("사용자 ID: " + workerId + " 는 이미 공고 ID: " + jobPostId + " 에 지원했습니다.");
        this.workerId = workerId;
        this.jobPostId = jobPostId;
    }
}
