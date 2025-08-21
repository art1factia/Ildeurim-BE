package com.example.Ildeurim.dto.OTP;

import com.example.Ildeurim.commons.enums.UserType;
import jakarta.validation.constraints.*;

public record OtpVerifyReq(
        @NotBlank
        @Pattern(regexp = "^\\+?[1-9]\\d{6,14}$")
        String phone,
        @NotBlank @Size(min = 6, max = 6)
        @Pattern(regexp = "^[0-9]{6}$")
        String code,
        @NotNull
        UserType userType       // ★ 어떤 역할로 로그인할지 명시 (WORKER/EMPLOYER)
) {}