package com.example.Ildeurim.dto.jobpost;

import com.example.Ildeurim.commons.enums.jobpost.*;
import com.example.Ildeurim.domain.JobPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class JobPostDetailRes {
    private Long jobPostId;
    private LocalDateTime createdAt;       // 생성일
    private LocalDateTime updatedAt;       // 수정일
    private String title;                  // 모집 제목

    private PaymentType paymentType;       // 급여 유형
    private Long payment;                  // 급여 금액

    private String location;               // 근무 위치

    private String content;                  // 상세 내용
    private LocalTime workStartTime; //근무 시작 시간
    private LocalTime workEndTime; // 근무 마감 시간
    private WorkType workType;
    private Set<WorkDays> workDays;
    private Integer workDaysCount;
    private JobPostStatus status;          // OPEN / CLOSED
    private Boolean careerRequirement; //경력
    private EducationRequirement educationRequirement; // 학력
    private EmploymentType employmentType;
    private Set<JobField> jobFields;

    private Set<ApplyMethod> applyMethods; // 지원 방법 (간편지원, 전화, 이메일 등)

    private LocalDateTime expiryDate;      // 마감일

    private String employerName;
    private String employerPhone;


    public static JobPostDetailRes of(JobPost jobPost) {
        return JobPostDetailRes.builder()
                .jobPostId(jobPost.getId())
                .createdAt(jobPost.getCreatedAt())
                .updatedAt(jobPost.getUpdatedAt())
                .title(jobPost.getTitle())
                .paymentType(jobPost.getPaymentType())
                .payment(jobPost.getPayment())
                .location(jobPost.getLocation())
                .content(jobPost.getContent())
                .workStartTime(jobPost.getWorkStartTime())
                .workEndTime(jobPost.getWorkEndTime())
                .workType(jobPost.getWorkType())
                .workDaysCount(jobPost.getWorkDaysCount())
                .workDays(jobPost.getWorkDays())
                .status(jobPost.getStatus())
                .careerRequirement(jobPost.getCareerRequirement())
                .educationRequirement(jobPost.getEducationRequirement())
                .employmentType(jobPost.getEmploymentType())
                .jobFields(jobPost.getJobFields())
                .applyMethods(jobPost.getApplyMethods())
                .expiryDate(jobPost.getExpiryDate())
                .employerName(jobPost.getEmployer().getName())
                .employerPhone(jobPost.getEmployer().getPhoneNumber())
                .build();
    }
}
