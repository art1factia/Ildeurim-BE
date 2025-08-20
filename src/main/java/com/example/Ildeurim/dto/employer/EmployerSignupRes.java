package com.example.Ildeurim.dto.employer;

public record EmployerSignupRes(
        Long employerId,
        String accessToken,
        long expiresAtEpochSeconds) {
}
