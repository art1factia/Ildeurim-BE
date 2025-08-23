package com.example.Ildeurim.dto.jobpost;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record JobPostUpdateReq (
        String title,
        String content,
        String paymentType,
        Long payment,
        String location,
        Integer restTime,
        String workType,
        List<String> workDays,
        Integer workDaysCount,
        Boolean careerRequirement,
        List<String> applyMethods,
        String jobField,
        LocalDateTime startDate,
        LocalDateTime expiryDate,
        String status,
        LocalTime workStartTime,
        LocalTime workEndTime,
        String educationRequirement,
        String employmentType,
        String workPlace
){

}
