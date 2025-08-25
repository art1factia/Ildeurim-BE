package com.example.Ildeurim.controller;

import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.dto.ApiResponse;
import com.example.Ildeurim.dto.application.req.*;
import com.example.Ildeurim.dto.application.res.ApplicationDetailRes;
import com.example.Ildeurim.dto.application.res.ApplicationListRes;
import com.example.Ildeurim.dto.application.res.ApplicationRes;
import com.example.Ildeurim.dto.application.res.SimpleApplicationRes;
import com.example.Ildeurim.service.ApplicationService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    /*--------------------- 간편 지원 관련 컨트롤러 ---------------------*/

    /*초안 생성*/
    @PostMapping("/{jobPostId}/apply")
    public ResponseEntity<ApiResponse> createApplication(@PathVariable Long jobPostId, @RequestBody @Valid ApplicationCreateReq req) {
        Long applicationId = applicationService.createApplication(req, jobPostId);
        Map<String, Long> data = Collections.singletonMap("applicationId", applicationId);

        ApiResponse<Map<String, Long>> apiResponse = new ApiResponse<>(
                true, HttpStatus.CREATED.value(), "지원서 초안이 성공적으로 생성되었습니다.", data
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }


    /*임시 저장,수정*/
    @PatchMapping("/{id}/answers")
    public ResponseEntity<ApiResponse> addAnswerToApplication(@PathVariable Long id, @RequestBody ApplicationAnswerUpdateReq req) {
        applicationService.addAnswerToApplication(id, req);
        Map<String, Long> data = Collections.singletonMap("applicationId", id);

        ApiResponse<Map<String, Long>> apiResponse = new ApiResponse<>(
                true, HttpStatus.OK.value(), "답변이 성공적으로 임시 저장되었습니다.", data
        );
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }


    /*최종 지원서 제출*/
    @PatchMapping("/{id}/submit")
    public ResponseEntity<ApiResponse> submitApplication(
            @PathVariable Long id
    ) {
        ApplicationRes res = applicationService.submitApplication(id);

        ApiResponse<ApplicationRes> apiResponse = new ApiResponse<>(
                true, HttpStatus.OK.value(), "지원서가 성공적으로 제출되었습니다.", res
        );
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }



    /*지원서 수정*/
//    @PatchMapping("/{id}/answer")
//    public ResponseEntity<ApiResponse> modifyApplication(
//            @PathVariable Long applicationId, @RequestBody ApplicationModifyReq req
//    ) {
//        applicationService.modifyApplication(applicationId,req);
//        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, HttpStatus.OK.value(), "지원서가 성공적으로 수정되었습니다.")
//        );
//    }

    /*지원서 삭제*/
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(true, HttpStatus.OK.value(), "지원서가 성공적으로 삭제되었습니다.")
        );
    }

    /*--------------------- 전화 지원 추가 관련 컨트롤러---------------------*/
    @PostMapping("/{jobPostId}/phoneApply")
    public ResponseEntity<ApiResponse> phoneApply(@PathVariable long jobPostId) {
        ApplicationRes res = applicationService.createPhoneApplication(jobPostId);
//        Map<String, Long> data = Collections.singletonMap("applicationId", applicationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, HttpStatus.CREATED.value(), "전화 지원이 성공적으로 처리되었습니다.", res)
        );
    }

    @PatchMapping("/{id}/confirmPhoneApply")
    public ResponseEntity<ApiResponse> phoneApplyConfirm(@PathVariable long id) {
        ApplicationRes res = applicationService.confirmPhoneApplication(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse(true, HttpStatus.OK.value(), "전화 지원이 확정되었습니다.", res)
        );
    }


    /*--------------------- 구직자의 지원 조회 관련 컨트롤러---------------------*/
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getMyApplications() {
        List<SimpleApplicationRes> applications = applicationService.getMyApplications();

        ApiResponse<List<SimpleApplicationRes>> apiResponse = new ApiResponse<>(
                true, HttpStatus.OK.value(), "지원 내역 조회 성공", applications
        );
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    /*--------------------- 고용주 지원자 조회 관련 컨트롤러---------------------*/
//    @GetMapping("/jobPosts/applications")
//    public ResponseEntity<ApiResponse> getApplicantsList(
//            @PathVariable Long jobPostId
//    ) {
//        List<ApplicationListRes> resultList = applicationService.getApplicantsList(jobPostId);
//
//        ApiResponse apiResponse = new ApiResponse<>(
//                true, HttpStatus.OK.value(), "지원자 목록 조회 성공", resultList
//        );
//
//        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
//    }

    /*지원 상태 변화*/
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody ApplicationStatusUpdateReq req
    ) {

        applicationService.updateApplicationStatus(id, req.toApplicationStatus());

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "지원서 상태 변경 성공")
        );
    }

    /*상세 이력서 홛인*/
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getApplicationDetails(@PathVariable Long id) {
        ApplicationDetailRes res = applicationService.getApplicationDetails(id);

        ApiResponse<ApplicationDetailRes> apiResponse = new ApiResponse<>(
                true, HttpStatus.OK.value(), "지원서 상세 조회 성공", res
        );

        return ResponseEntity.ok(apiResponse);
    }
}

//    /*일괄 탈락 처리*/
//    @PatchMapping("/")
//    public ResponseEntity<ApiResponse> rejectNonHiredApplicants(
//            @PathVariable Long jobPostId
//    ) {
//        applicationService.rejectNonHiredApplicants(jobPostId);
//
//        return ResponseEntity.ok(
//                new ApiResponse<>(true, HttpStatus.OK.value(), "불합격자 처리 완료")
//        );
//    }
//git 확인




