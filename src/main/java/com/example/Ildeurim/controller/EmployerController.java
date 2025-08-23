package com.example.Ildeurim.controller;

import com.example.Ildeurim.auth.AuthContext;
import com.example.Ildeurim.dto.ApiResponse;
import com.example.Ildeurim.dto.career.CareerRes;
import com.example.Ildeurim.dto.employer.*;
import com.example.Ildeurim.service.CareerService;
import com.example.Ildeurim.service.EmployerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/employers")
public class EmployerController {

    private final EmployerService employerService;
    private final CareerService careerService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> createEmployer(@RequestBody EmployerCreateReq req) {
        EmployerSignupRes res = employerService.signup(AuthContext.principal().get(), req);
        return ResponseEntity.ok(new ApiResponse(true, 201, "create employer success", res));

    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> me() {
        EmployerDetailRes res = employerService.me();
        return ResponseEntity.ok(new ApiResponse(true, 201, "get employer me success", res));
    }

    @GetMapping("/{workerId}/careers")
    public ResponseEntity<ApiResponse> getWorkerCareers(@PathVariable Long workerId) {
        List<CareerRes> res = careerService.getWorkerPublicCareerList(workerId);
        return ResponseEntity.ok(new ApiResponse(true, 200, "get employer careers", res));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse> updateMe(@RequestBody EmployerUpdateReq req) {
        EmployerRes res = employerService.update(req);
        return ResponseEntity.ok(new ApiResponse(true, 200, "update employer me success", res));
    }

}
