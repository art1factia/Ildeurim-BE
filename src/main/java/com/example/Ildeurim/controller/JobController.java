// JobController.java
package com.example.Ildeurim.controller;

import com.example.Ildeurim.auth.AuthContext;
import com.example.Ildeurim.domain.Job;
import com.example.Ildeurim.dto.ApiResponse;
import com.example.Ildeurim.dto.job.*;
import com.example.Ildeurim.repository.JobRepository;
import com.example.Ildeurim.service.JobService;
import com.example.Ildeurim.service.ObjectStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.Duration;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;
    private final JobRepository jobRepository;
    private final ObjectStorageService storage;

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
    @PatchMapping(value = "/{id}/contract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> upsertContract(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        JobRes res = jobService.upsertContract(id, file);
        return ResponseEntity.ok(new ApiResponse(true, 200, "Contract updated", res));
    }

    // 예시: GET /jobs/{id}/contract/download
    @GetMapping("/{id}/contract/download")
    public ResponseEntity<Void> downloadContract(@PathVariable Long id) {
        Long userId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("job not found"));

        // 권한 체크 (예: 본인 워커이거나 고용주/관리자 등)
        if (!job.getWorker().getId().equals(userId) /* && !isEmployerOrAdmin(userId, job) */) {
            throw new AccessDeniedException("no access to contract");
        }
        if (job.getContractUrl() == null || job.getContractUrl().isBlank()) {
            throw new EntityNotFoundException("contract not uploaded");
        }

        String filename = "contract-" + id + ".pdf"; // 확장자 모르면 적당히 지정
        String presigned = storage.presignContractDownload(job.getContractUrl(),
                Duration.ofMinutes(10), filename);

        // 302 redirect (클라이언트가 직접 S3로 다운로드)
        return ResponseEntity.status(302).location(URI.create(presigned)).build();

        // 또는 JSON으로 반환하고, 프런트에서 window.location = url;
        // return ResponseEntity.ok(Map.of("url", presigned));
    }

}
