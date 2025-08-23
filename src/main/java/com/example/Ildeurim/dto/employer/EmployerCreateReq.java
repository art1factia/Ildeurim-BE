package com.example.Ildeurim.dto.employer;

import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.domain.Employer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Set;
import java.util.stream.Collectors;

public record EmployerCreateReq(
    @NotBlank String name,
    @Email
    @NotBlank String email,
    @NotBlank String bossName,
    @NotBlank @Pattern(regexp = "^\\+?[1-9]\\d{6,14}$") String phoneNumber,
    @NotBlank String companyName,
    @NotBlank String companyLocation,
    @NotBlank @Pattern(
            regexp = "^\\d{3}-\\d{2}-\\d{5}$",
            message = "사업자번호 형식이 올바르지 않습니다. 예: 123-45-67890"
    ) String companyNumber,
    Set<String> jobFields
) {
        public Employer toEntity() {
            return Employer.builder()
                    .name(name)
                    .email(email)
                    .bossName(bossName)
                    .phoneNumber(phoneNumber)
                    .companyName(companyName)
                    .companyLocation(companyLocation)
                    .companyNumber(companyNumber)
                    .jobFields(jobFields.stream().map(JobField::fromLabel).collect(Collectors.toSet()))
                    .build();
        }
    }
