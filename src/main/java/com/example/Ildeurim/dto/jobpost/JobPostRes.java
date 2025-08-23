package com.example.Ildeurim.dto.jobpost;

import com.example.Ildeurim.commons.enums.jobpost.*;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.domain.JobPost;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
        Integer workDaysCount,
        Boolean careerRequirement,
        Set<ApplyMethod> applyMethods,
        JobField jobField,
        LocalDateTime startDate,
        LocalDateTime expiryDate,
        JobPostStatus status,
        LocalTime workStartTime,
        LocalTime workEndTime,
        EducationRequirement educationRequirement,
        EmploymentType employmentType,
        Boolean saveQuestionList,
        WorkPlace workPlace,
        Long employerId,
        String employerName
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
                j.getWorkDaysCount(),
                j.getCareerRequirement(),
                j.getApplyMethods(),
                j.getJobField(),
                j.getStartDate(),
                j.getExpiryDate(),
                j.getStatus(),
                j.getWorkStartTime(),
                j.getWorkEndTime(),
                j.getEducationRequirement(),
                j.getEmploymentType(),
                j.getSaveQuestionList(),
                j.getWorkPlace(),
                j.getEmployer().getId(),
                j.getEmployer().getName()

        );
    }
}
