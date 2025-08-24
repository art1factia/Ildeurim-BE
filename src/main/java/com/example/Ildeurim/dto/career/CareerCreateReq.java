package com.example.Ildeurim.dto.career;

import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;

import java.time.LocalDate;

public record CareerCreateReq(
        String title,
        String companyName,
        String startDate,
        String endDate,        // ❗ 필수
        String workplace,
        String mainDuties,
        Boolean isOpening,
        String jobField         // 단일 Enum
) {}
