package com.example.Ildeurim.controller;

import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.dto.ApiResponse;
import com.example.Ildeurim.dto.employer.EmployerDetailRes;
import com.example.Ildeurim.dto.employer.EmployerSignupRes;
import com.example.Ildeurim.dto.worker.WorkerDetailRes;
import com.example.Ildeurim.repository.EmployerRepository;
import com.example.Ildeurim.service.EmployerService;
import com.example.Ildeurim.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/employers")
public class EmployerController {

    private final EmployerService employerService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> me() {
        EmployerDetailRes res = employerService.me();
        return ResponseEntity.ok(new ApiResponse(true, 201, "w", res));
    }

}
