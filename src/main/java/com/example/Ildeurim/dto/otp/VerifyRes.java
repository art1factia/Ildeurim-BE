package com.example.Ildeurim.dto.otp;

public record VerifyRes(
        String phone,
        boolean isNewbie,
        String accessToken
) {
}
