package com.example.Ildeurim.command;

import com.example.Ildeurim.commons.enums.jobpost.JobField;

import java.util.Optional;
import java.util.Set;

public record EmployerUpdateCmd(
        Optional<String> name,
        Optional<String> email,
        Optional<String> bossName,
        Optional<String> phoneNumber,
        Optional<String> companyName,
        Optional<String> companyLocation,
        Optional<String> companyNumber,
        Optional<Set<JobField>> jobFields
) {

}
