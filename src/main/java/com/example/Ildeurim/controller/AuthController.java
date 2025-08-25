package com.example.Ildeurim.controller;

import com.example.Ildeurim.commons.enums.UserType;
import com.example.Ildeurim.dto.ApiResponse;
import com.example.Ildeurim.dto.otp.*;
import com.example.Ildeurim.jwt.JwtUtil;
import com.example.Ildeurim.repository.EmployerRepository;
import com.example.Ildeurim.repository.WorkerRepository;
import com.example.Ildeurim.service.EmployerService;
import com.example.Ildeurim.service.SmsService;
import com.example.Ildeurim.service.WorkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SmsService smsService;
    private final JwtUtil jwtUtil;
    private final WorkerService workerService;
    private final EmployerService employerService;
    private final WorkerRepository workerRepository;
    private final EmployerRepository employerRepository;


    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@Valid @RequestBody OtpSendReq req) {
        String phone = req.phone();
        smsService.sendVerificationCode(phone);
        return ResponseEntity.ok("Verification code sent");
    }
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<?>> verifyCode(@Valid @RequestBody OtpVerifyReq req){
        boolean v = smsService.verifyCode(req.phone(), req.code());
        boolean exists = workerRepository.existsByPhoneNumber(req.phone());
                if(!exists) {
                    exists = employerRepository.existsByPhoneNumber(req.phone());
                }

        VerifyRes res = new VerifyRes(req.phone(), exists);
        return ResponseEntity.ok(new ApiResponse<>(v, 200, "verification" + (v ? "success" : "fail"), res));
    }
    @PostMapping("/signup-token")
    public ResponseEntity<ApiResponse<?>> getSignupToken(@Valid @RequestBody getSignupTokenReq req) {

        boolean exists = (req.userType() == UserType.WORKER)
                ? workerRepository.existsByPhoneNumber(req.phone())
                : employerRepository.existsByPhoneNumber(req.phone());

        if (exists) {
            Optional<Long> userIdOpt = (req.userType() == UserType.WORKER)
                    ? workerRepository.findIdByPhoneNumber(req.phone())
                    : employerRepository.findIdByPhoneNumber(req.phone());
            String access = jwtUtil.generateAccessToken(userIdOpt.get(), req.userType(), req.phone(), 360);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, 200, "existing user login", new JwtRes(access, false))
            );
        } else {
            String signup = jwtUtil.generateSignupToken(req.userType(), req.phone(), 15);
            // 프론트는 이 토큰으로 POST /{type} 화면에서 제출
            return ResponseEntity.ok(
                    new ApiResponse<>(true, 200, "ok", new SignupJwtRes(signup, true))
            );
        }
    }

}
