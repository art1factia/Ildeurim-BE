package com.example.Ildeurim.dto.career;

import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.domain.Career;

import java.time.LocalDate;

public record CareerRes(
        Long id,
        Long workerId,
        String title,
        String companyName,
        LocalDate startDate,
        LocalDate endDate,
        WorkPlace workplace,
        String mainDuties,
        Boolean isOpening,
        JobField jobField
) {
    public static CareerRes from(Career career) {
        return new CareerRes(
                career.getId(),
                career.getWorker() != null ? career.getWorker().getId() : null,
                career.getTitle(),
                career.getCompanyName(),
                career.getStartDate(),
                career.getEndDate(),
                career.getWorkplace(),
                career.getMainDuties(),
                career.getIsOpening(),
                career.getJobField()
        );
    }
}
