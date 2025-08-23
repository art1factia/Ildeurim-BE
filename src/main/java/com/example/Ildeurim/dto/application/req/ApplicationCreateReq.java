package com.example.Ildeurim.dto.application.req;

import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import com.example.Ildeurim.commons.enums.jobpost.ApplyMethod;
import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.domain.quickAnswer.AnswerList;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApplicationCreateReq {
    @NotNull private Long jobPostId;
    @NotNull private String applyMethod; //ApplyMethod
    @NotNull private Boolean isCareerIncluding;

    public ApplyMethod toApplyMethod() {
        try {
            return ApplyMethod.fromString(applyMethod); //
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 지원 방법입니다: " + applyMethod);
        }
    }

    public Application toEntity(JobPost jobPost, Worker worker) {
        return Application.builder()
                .submissionTime(LocalDateTime.now())
                .applicationStatus(ApplicationStatus.DRAFT)
                .applyMethod(toApplyMethod())
                .isCareerIncluding(isCareerIncluding)
                .jobPost(jobPost)
                .worker(worker)
                .answers(AnswerList.empty())
                .build();
    }
}