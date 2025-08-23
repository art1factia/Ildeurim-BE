package com.example.Ildeurim.dto.jobpost;

import com.example.Ildeurim.commons.converter.DateParsers;
import com.example.Ildeurim.commons.enums.jobpost.*;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.domain.JobPost;
import org.hibernate.jdbc.Work;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public record JobPostCreateReq(
        String title,
        String content,
        String paymentType,
        Long payment,
        String location,
        Integer restTime,
        String workType,
        List<String> workDays,
        Integer workNumber,
        Boolean careerRequirement,
        List<String> applyMethods,
        String jobField,
        String startDate,
        String expiryDate,
        String status,
        String workStartTime,
        String workEndTime,
        String educationRequirement,
        String employmentType,
        Boolean saveQuestionList,
        String workPlace
) {
    public JobPost toEntity() {
        return JobPost.builder()
                .title(title)
                .content(content)
                .paymentType(PaymentType.fromLabel(paymentType))
                .payment(payment)
                .location(location)
                .restTime(restTime)
                .workType(WorkType.fromLabel(workType))
                .workDays(workDays.stream().map(WorkDays::fromLabel).collect(Collectors.toSet()))
                .workDaysCount(workNumber)
                .careerRequirement(careerRequirement)
                .applyMethods(applyMethods.stream().map(ApplyMethod::fromLabel).collect(Collectors.toSet()))
                .jobField(JobField.fromLabel(jobField))

                .startDate(DateParsers.parseLocalDateTime(startDate)) //TODO: LocalDateTime 형변환
                .expiryDate(DateParsers.parseLocalDateTime(expiryDate)) //TODO: LocalDateTime 형변환

                .status(JobPostStatus.fromLabel(status))

                .workStartTime(DateParsers.parseLocalTime(workStartTime))//TODO: LocalTime 형변환

                .workEndTime(DateParsers.parseLocalTime(workEndTime))
                .educationRequirement(EducationRequirement.fromLabel(educationRequirement))
                .employmentType(EmploymentType.fromLabel(employmentType))
                .saveQuestionList(saveQuestionList)
                .workPlace(WorkPlace.fromLabel(workPlace))
                .build();

    }
}
