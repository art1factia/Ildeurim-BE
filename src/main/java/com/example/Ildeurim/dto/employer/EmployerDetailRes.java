package com.example.Ildeurim.dto.employer;


import com.example.Ildeurim.domain.Employer;
import com.example.Ildeurim.commons.enums.jobpost.JobField;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public record EmployerDetailRes(
        Long id,
        String name,
        String email,
        String bossName,
        String phoneNumber,
        String companyName,
        String companyLocation,
        String companyNumber,
        List<JobField> jobFields,

        long reviewCount,
        JSONObject questionList
) {
    public static EmployerDetailRes from(Employer e, long reviewCount, JSONObject questionList) {
        return new EmployerDetailRes(
                e.getId(),
                e.getName(),
                e.getEmail(),
                e.getBossName(),
                e.getPhoneNumber(),
                e.getCompanyName(),
                e.getCompanyLocation(),
                e.getCompanyNumber(),
                e.getJobFields() != null ? List.copyOf(e.getJobFields()) : List.of(),

                reviewCount,
                questionList == null ? new JSONObject() : questionList
        );
    }
}
