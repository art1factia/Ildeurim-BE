package com.example.Ildeurim.controller;

import com.example.Ildeurim.dto.ApiResponse;
import com.example.Ildeurim.dto.OTP.JwtRes;
import com.example.Ildeurim.dto.OTP.OtpSendReq;
import com.example.Ildeurim.dto.OTP.OtpVerifyReq;
import com.example.Ildeurim.jwt.JwtUtil;
import com.example.Ildeurim.service.EmployerService;
import com.example.Ildeurim.service.SmsService;
import com.example.Ildeurim.service.WorkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SmsService smsService;
    private final JwtUtil jwtUtil;
    private final WorkerService workerService;
    private final EmployerService employerService;


    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@Valid @RequestBody OtpSendReq req) {
        String phone = req.phone();
        smsService.sendVerificationCode(phone);
        return ResponseEntity.ok("Verification code sent");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody OtpVerifyReq req) {
        String phone = req.phone();
        String code = req.code();


        Long userId = switch (req.userType()) {
            case WORKER -> workerService.ensureByPhone(req.phone());    // 없으면 생성 후 id 리턴
            case EMPLOYER -> employerService.ensureByPhone(req.phone()); // 동일
        };

        if (smsService.verifyCode(phone, code)) {
            String token = jwtUtil.generateToken(userId, req.userType(), req.phone());
            return ResponseEntity.ok(
                    new ApiResponse<>(true, 200, "ok", new JwtRes(token))
            );
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid code");
        }
    }
}
