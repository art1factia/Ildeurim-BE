package com.example.Ildeurim.controller;


import com.example.Ildeurim.dto.ApiResponse;
import com.example.Ildeurim.dto.application.res.ApplicationListRes;
import com.example.Ildeurim.dto.application.res.ApplicationRes;
import com.example.Ildeurim.dto.jobpost.*;
import com.example.Ildeurim.service.ApplicationService;
import com.example.Ildeurim.service.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/jobPosts")
public class JobPostController {
    private final JobPostService jobPostService;
    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApiResponse> createJobPost(@RequestBody JobPostCreateReq req) {
        JobPostRes res = jobPostService.create(req);
        return ResponseEntity.ok(new ApiResponse(true, 200, "Job post created", res));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getJobPostList(@RequestBody JobPostFilter req) {
        List<SimpleJobPostRes> res = jobPostService.getJobPostList(req);
        return ResponseEntity.ok(new ApiResponse(true, 200, "Job post list", res));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getJobPost(@PathVariable long id) {
        JobPostDetailRes res = jobPostService.getJobPost(id);
        return ResponseEntity.ok(new ApiResponse(true, 200, "Job post", res));
    }

    @GetMapping("/{id}/applications")
    public ResponseEntity<ApiResponse> getJobPostApplications(@PathVariable long id) {
        List<ApplicationListRes> res = applicationService.getApplicantsList(id);
        return ResponseEntity.ok(new ApiResponse(true, 200, "Job post applications", res));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateJobPost(@PathVariable long id, @RequestBody JobPostUpdateReq req) {
        JobPostRes res = jobPostService.update(id, req);
        return ResponseEntity.ok(new ApiResponse(true, 200, "Job post updated", res));
    }

    @PatchMapping("/{id}/end")
    public ResponseEntity<ApiResponse> endJobPost(@PathVariable long id) {
        jobPostService.end(id);
        return ResponseEntity.ok(new ApiResponse(true, 200, "Job post ended", null));
    }
    @PatchMapping("/{id}/questionList")
    public ResponseEntity<ApiResponse> updateQuestionList(@PathVariable long id, @RequestBody JobPostQuestionListUpdateReq req) {
        JobPostRes res = jobPostService.updateQuestionList(id, req);
        return ResponseEntity.ok(new ApiResponse(true, 200, "Job post question list updated", res));
    }


}
