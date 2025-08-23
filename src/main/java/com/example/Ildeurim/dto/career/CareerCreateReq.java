package com.example.Ildeurim.dto.career;

import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;

import java.time.LocalDate;

public record CareerCreateReq(
        String title,
        String companyName,
        LocalDate startDate,
        LocalDate endDate,        // ❗ 필수
        WorkPlace workplace,
        String mainDuties,
        Boolean isOpening,
        JobField jobField         // 단일 Enum
) {}
