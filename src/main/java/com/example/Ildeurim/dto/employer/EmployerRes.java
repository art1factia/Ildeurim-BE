package com.example.Ildeurim.dto.employer;

import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.domain.Employer;
import org.json.JSONObject;

import java.util.List;

public record EmployerRes(
        Long id,
        String name,
        String email,
        String bossName,
        String phoneNumber,
        String companyName,
        String companyLocation,
        String companyNumber,
        List<JobField> jobFields
) {
    public static EmployerRes from(Employer e) {
        return new EmployerRes(
                e.getId(),
                e.getName(),
                e.getEmail(),
                e.getBossName(),
                e.getPhoneNumber(),
                e.getCompanyName(),
                e.getCompanyLocation(),
                e.getCompanyNumber(),
                e.getJobFields() != null ? List.copyOf(e.getJobFields()) : List.of()
        );
    }

}