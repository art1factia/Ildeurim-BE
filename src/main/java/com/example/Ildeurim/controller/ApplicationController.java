package com.example.Ildeurim.controller;

import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.dto.ApiResponse;
import com.example.Ildeurim.dto.application.req.ApplicationAnswerUpdateReq;
import com.example.Ildeurim.dto.application.req.ApplicationCreateReq;
import com.example.Ildeurim.dto.application.req.ApplicationModifyReq;
import com.example.Ildeurim.dto.application.req.ApplicationStatusUpdateReq;
import com.example.Ildeurim.dto.application.res.ApplicationListRes;
import com.example.Ildeurim.dto.application.res.ApplicationRes;
import com.example.Ildeurim.dto.application.res.SimpleApplicationRes;
import com.example.Ildeurim.service.ApplicationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ApplicationController {
    private final ApplicationService applicationService;

    /*--------------------- 간편 지원 관련 컨트롤러 ---------------------*/

    /*초안 생성*/
    @PostMapping("/applications/{jobPostId}/apply")
    public ResponseEntity<ApiResponse> createApplication(@PathVariable Long jobPostId, @RequestBody ApplicationCreateReq req) {
        Long applicationId=applicationService.addApplication(req);
        Map<String, Long> data = Collections.singletonMap("applicationId", applicationId);

        ApiResponse<Map<String, Long>> apiResponse = new ApiResponse<>(
                true, HttpStatus.CREATED.value(), "지원서 초안이 성공적으로 생성되었습니다.", data
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /*임시 저장*/
    @PatchMapping("/applications/{applicationId}/answers")
    public ResponseEntity<ApiResponse> addAnswerToApplication(@RequestBody ApplicationAnswerUpdateReq req) {
        applicationService.addAnswerToApplication(req);

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                true, HttpStatus.OK.value(), "답변이 성공적으로 임시 저장되었습니다."
        );
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    /*최종 지원서 제출*/
    @PostMapping("/applications/{applicationId}/submit")
    public ResponseEntity<ApiResponse> submitApplication(
            @RequestBody ApplicationStatusUpdateReq req
    ) {
        ApplicationRes res = applicationService.submitApplication(req);

        ApiResponse<ApplicationRes> apiResponse = new ApiResponse<>(
                true, HttpStatus.OK.value(), "지원서가 성공적으로 제출되었습니다.", res
        );
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    /*지원서 수정*/
    @PatchMapping("/applications/{applicationId}/answer")
    public ResponseEntity<ApiResponse> modifyApplication(
            @PathVariable Long applicationId, @RequestBody ApplicationModifyReq req
    ) {
        // 보안: PathVariable의 ID와 DTO의 ID가 일치하는지 확인
        if (!applicationId.equals(req.applicationId())) {
            throw new IllegalArgumentException("지원서 ID가 일치하지 않습니다.");
        }

        applicationService.modifyApplication(req);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, HttpStatus.OK.value(), "지원서가 성공적으로 수정되었습니다.")
        );
    }

    @DeleteMapping("/applications/{applicationId}")
    public ResponseEntity<ApiResponse<Void>> abandonApplication(@PathVariable Long applicationId) {
        applicationService.deleteApplication(applicationId);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(true, HttpStatus.OK.value(), "지원서가 성공적으로 삭제되었습니다.")
        );
    }

    /*--------------------- 구직자의 지원 조회 관련 컨트롤러---------------------*/
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getMyApplications(@RequestParam Long workerId) {
        List<SimpleApplicationRes> applications = applicationService.getMyApplications(workerId);

        ApiResponse<List<SimpleApplicationRes>> apiResponse = new ApiResponse<>(
                true, HttpStatus.OK.value(), "지원 내역 조회 성공", applications
        );
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    /*--------------------- 고용주 지원자 조회 관련 컨트롤러---------------------*/
    @GetMapping("/jobPosts/applications")
    public ResponseEntity<ApiResponse> getApplicantsList(
            @PathVariable Long jobPostId
    ) {
        List<ApplicationListRes> resultList = applicationService.getApplicantsList(jobPostId);

        ApiResponse apiResponse = new ApiResponse<>(
                true, HttpStatus.OK.value(), "지원자 목록 조회 성공", resultList
        );

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }



}

