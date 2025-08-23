package com.example.Ildeurim.dto.career;

import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;

import java.time.LocalDate;

public record CareerUpdateReq(
        String title,
        String companyName,
        LocalDate startDate,
        LocalDate endDate,
        WorkPlace workplace,
        String mainDuties,
        Boolean isOpening,
        JobField jobField
) {}
