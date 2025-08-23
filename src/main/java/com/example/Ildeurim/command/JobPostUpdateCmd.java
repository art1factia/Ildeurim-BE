package com.example.Ildeurim.command;

import com.example.Ildeurim.commons.enums.jobpost.*;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

public record JobPostUpdateCmd(
        Optional<String> title,
        Optional<PaymentType> paymentType,
        Optional<Long> payment,
        Optional<String> location,
        Optional<String> content,
        Optional<LocalTime> workStartTime,
        Optional<LocalTime> workEndTime,
        Optional<WorkType> workType,
        Optional<Set<WorkDays>> workDays,
        Optional<Integer> workDaysCount,
        Optional<JobPostStatus> status,
        Optional<Boolean> careerRequirement,
        Optional<EducationRequirement> educationRequirement,
        Optional<EmploymentType> employmentType,
        Optional<JobField> jobField,
        Optional<Set<ApplyMethod>> applyMethods,
        Optional<LocalDateTime> startDate,
        Optional<LocalDateTime> expiryDate,
        Optional<WorkPlace> workPlace
) {
    private static <T> Optional<T> nn(Optional<T> o) { return o == null ? Optional.empty() : o; }
    public JobPostUpdateCmd {
        title = nn(title);
        paymentType = nn(paymentType);
        payment = nn(payment);
        location = nn(location);
        content = nn(content);
        workStartTime = nn(workStartTime);
        workEndTime = nn(workEndTime);
        workType = nn(workType);
        workDays = nn(workDays);
        workDaysCount = nn(workDaysCount);
        status = nn(status);
        careerRequirement = nn(careerRequirement);
        educationRequirement = nn(educationRequirement);
        employmentType = nn(employmentType);
        jobField = nn(jobField);
        applyMethods = nn(applyMethods);
        startDate = nn(startDate);
        expiryDate = nn(expiryDate);
        workPlace = nn(workPlace);
    }
}

