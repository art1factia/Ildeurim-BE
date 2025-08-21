package com.example.Ildeurim.dto.jobpost;

import com.example.Ildeurim.commons.enums.jobpost.*;
import com.example.Ildeurim.domain.Employer;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.dto.employer.EmployerRes;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public record JobPostRes(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String title,
        String content,
        PaymentType paymentType,
        Long payment,
        String location,
        Integer restTime,
        WorkType workType,
        Set<WorkDays> workDays,
        Integer workNumber,
        Boolean careerRequirement,
        Set<ApplyMethod> applyMethods,
        Set<JobField> jobFields,
        LocalDateTime startDate,
        LocalDateTime expiryDate,
        JobPostStatus status,
        LocalTime workStartTime,
        LocalTime workEndTime,
        EducationRequirement educationRequirement,
        EmploymentType employmentType
) {
    public static JobPostRes from(JobPost j) {
        return new JobPostRes(
                j.getId(),
                j.getCreatedAt(),
                j.getUpdatedAt(),
                j.getTitle(),
                j.getContent(),
                j.getPaymentType(),
                j.getPayment(),
                j.getLocation(),
                j.getRestTime(),
                j.getWorkType(),
                j.getWorkDays(),
                j.getWorkNumber(),
                j.getCareerRequirement(),
                j.getApplyMethods(),
                j.getJobFields(),
                j.getStartDate(),
                j.getExpiryDate(),
                j.getStatus(),
                j.getWorkStartTime(),
                j.getWorkEndTime(),
                j.getEducationRequirement(),
                j.getEmploymentType()

        );
    }
}
