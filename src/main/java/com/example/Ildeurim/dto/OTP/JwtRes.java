package com.example.Ildeurim.dto.OTP;

import com.example.Ildeurim.commons.enums.UserType;

public record JwtRes(
        String accessToken,
        boolean isNewbie
) {}
