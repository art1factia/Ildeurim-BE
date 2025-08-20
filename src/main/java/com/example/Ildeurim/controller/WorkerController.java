package com.example.Ildeurim.controller;


import com.example.Ildeurim.dto.ApiResponse;
import com.example.Ildeurim.dto.employer.EmployerDetailRes;
import com.example.Ildeurim.dto.worker.WorkerDetailRes;
import com.example.Ildeurim.service.EmployerService;
import com.example.Ildeurim.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/workers")
public class WorkerController {
    private final WorkerService workerService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> me() {
        WorkerDetailRes res = workerService.me();
        return ResponseEntity.ok(new ApiResponse(true, 201, "w", res));
    }

}
