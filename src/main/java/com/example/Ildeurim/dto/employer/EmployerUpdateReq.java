package com.example.Ildeurim.dto.employer;

import java.util.List;
import java.util.Set;

public record EmployerUpdateReq(
        String name,
        String email,
        String bossName,
        String phoneNumber,
        String companyName,
        String companyLocation,
        String companyNumber,
        List<String> jobFields
) {
}
