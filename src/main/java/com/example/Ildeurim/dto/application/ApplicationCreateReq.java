package com.example.Ildeurim.dto.application;

import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import com.example.Ildeurim.commons.enums.jobpost.ApplyMethod;
import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.domain.Worker;

import java.time.LocalDateTime;

public class ApplicationCreateReq {
    private Long jobPostId;
    private Long workerId;
    private ApplyMethod applyMethod;
    private Boolean isCareerIncluding;

    public Application toEntity(JobPost jobPost, Worker worker) {
        return Application.builder()
                .submissionTime(LocalDateTime.now())
                .applicationStatus(ApplicationStatus.DRAFT)
                .applyMethod(applyMethod)
                .isCareerIncluding(isCareerIncluding)
                .jobPost(jobPost)
                .worker(worker)
                .build();
    }
}