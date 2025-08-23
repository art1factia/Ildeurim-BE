package com.example.Ildeurim.controller;


import com.example.Ildeurim.auth.AuthContext;
import com.example.Ildeurim.config.NcpStorageProps;
import com.example.Ildeurim.dto.ApiResponse;
import com.example.Ildeurim.dto.career.CareerRes;
import com.example.Ildeurim.dto.worker.*;
import com.example.Ildeurim.service.CareerService;
import com.example.Ildeurim.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/workers")
public class WorkerController {
    private final WorkerService workerService;
    private final CareerService careerService;
    private final S3Client s3Client;
    private final NcpStorageProps ncpStorageProps;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> createWorker(@RequestBody WorkerCreateReq req) {
        WorkerSignupRes res = workerService.signup(AuthContext.principal().get(), req);
        return ResponseEntity.ok(new ApiResponse(true, 201, "create worker success", res));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> me() {
        WorkerDetailRes res = workerService.me();
        return ok(new ApiResponse(true, 201, "get me success", res));
    }

    @GetMapping("/me/careers")
    public ResponseEntity<ApiResponse> meCareerList() {
        List<CareerRes> res = careerService.getWorkerCareerList();
        return ok(new ApiResponse(true, 201, "get me career success", res));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse> updateMe(@RequestBody WorkerUpdateReq req) {
        WorkerRes res = workerService.update(req);
        return ok(new ApiResponse(true, 200, "update me success", res));
    }
    @PatchMapping(value = "/me/profileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> updateMyProfileImage(@RequestPart("file") MultipartFile file) {
        String url = workerService.updateMyProfileImage(file);
        return ResponseEntity.ok(new ApiResponse<>(true, 200, "ok", new WorkerProfileImageRes(url)));
    }

}
