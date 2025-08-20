package com.example.Ildeurim.dto.jobpost;

import com.example.Ildeurim.commons.enums.jobpost.*;
import com.example.Ildeurim.domain.JobPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class JobPostCreateReq {
    private List<ApplyMethod> applyMethod;

    private String title;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private JobField jobField;

    private PaymentType paymentType;
    private Long payment;
    private String location;

    private Boolean careerRequirement;
    private EducationRequirement educationRequirement;
    private EmploymentType employmentType;

    private WorkType workType;
    private List<WorkDays> workDays;
    private LocalTime workStartTime;
    private LocalTime workEndTime;
    private Integer workDaysCount;

    private String content;

    public JobPost toEntity()
    { return JobPost.builder()
                .applyMethod(applyMethod)
                .title(title)
                .startDate(startDate)
                .expiryDate(expiryDate)
                .jobField(jobField)
                .paymentType(paymentType)
                .payment(payment)
                .location(location)
                .careerRequirement(careerRequirement)
                .educationRequirement(educationRequirement)
                .employmentType(employmentType)
                .workType(workType)
                .workDays(workDays)
                .workStartTime(workStartTime)
                .workEndTime(workEndTime)
                .workDaysCount(workDaysCount)
                .content(content)
                .build();
    }


}
