package com.example.Ildeurim.dto.application;

import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.domain.Career;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareerRes {
    private String companyName;
    private String mainDuties;
    private LocalDate startDate;
    private LocalDate endDate;
    private JobField jobField;
    private String duration;

    public static CareerRes from(Career career) {
        return CareerRes.builder()
                .companyName(career.getCompanyName())
                .mainDuties(career.getMainDuties())
                .startDate(career.getStartDate())
                .endDate(career.getEndDate())
                .jobField(career.getJobField())
                .duration(calculateDuration(career.getStartDate(), career.getEndDate()))
                .build();
    }

    private static String calculateDuration(LocalDate start, LocalDate end) {
        if (start == null || end == null) return null;
        int years = end.getYear() - start.getYear();
        return years > 0 ? years + "년 이상" : "1년 미만";
    }
}
