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
        String startDate,
        String expiryDate,
        String status,
        String workStartTime,
        String workEndTime,
        String educationRequirement,
        String employmentType,
        String workPlace
){

}
