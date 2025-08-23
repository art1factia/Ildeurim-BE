package com.example.Ildeurim.dto.worker;

public record WorkerSignupRes(
        Long workerId,
        String accessToken,
        long expiresAtEpochSeconds
) {
}
