package com.example.Ildeurim.dto.otp;

import com.example.Ildeurim.commons.enums.UserType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record getSignupTokenReq(
        @Pattern(regexp = "^\\+?[1-9]\\d{6,14}$")
        String phone,
        @NotNull
        UserType userType       // ★ 어떤 역할로 로그인할지 명시 (WORKER/EMPLOYER)
) {
}
