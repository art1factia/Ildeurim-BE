// JobController.java
package com.example.Ildeurim.controller;

import com.example.Ildeurim.dto.ApiResponse;
import com.example.Ildeurim.dto.job.*;
import com.example.Ildeurim.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    // 근로 생성
    @PostMapping
    public ResponseEntity<ApiResponse> createJob(@RequestBody JobCreateReq req) {
        JobRes res = jobService.create(req);
        return ResponseEntity.ok(new ApiResponse(true, 200, "Job created", res));
    }

    // 고용인 근로 목록 조회 (isWorking=true)
    @GetMapping("/worker/{workerId}")
    public ResponseEntity<ApiResponse> getJobList(@PathVariable Long workerId) {
        List<JobRes> res = jobService.getList(workerId);
        return ResponseEntity.ok(new ApiResponse(true, 200, "Job list", res));
    }

    // 근로 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getJob(@PathVariable Long id) {
        JobRes res = jobService.get(id); // <-- getJob -> get 로 수정
        return ResponseEntity.ok(new ApiResponse(true, 200, "Job detail", res));
    }

    // 근로 수정
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateJob(@PathVariable Long id, @RequestBody JobUpdateReq req) {
        JobRes res = jobService.update(id, req);
        return ResponseEntity.ok(new ApiResponse(true, 200, "Job updated", res));
    }

    // 근로 종료 (서비스에 end 메서드가 있다면 그대로 사용)
    @PatchMapping("/{id}/end")
    public ResponseEntity<ApiResponse> endJob(@PathVariable Long id) {
        jobService.end(id);
        return ResponseEntity.ok(new ApiResponse(true, 200, "Job ended", null));
    }

    // 계약서 추가/수정
    @PutMapping("/{id}/contract")
    public ResponseEntity<ApiResponse> upsertContract(@PathVariable Long id, @RequestBody JobContractReq req) {
        JobRes res = jobService.upsertContract(id, req);
        return ResponseEntity.ok(new ApiResponse(true, 200, "Contract updated", res));
    }
}
