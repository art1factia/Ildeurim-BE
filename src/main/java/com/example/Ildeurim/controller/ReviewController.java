package com.example.Ildeurim.controller;

import com.example.Ildeurim.dto.ApiResponse;
import com.example.Ildeurim.dto.review.ReviewCreateReq;
import com.example.Ildeurim.dto.review.ReviewRes;
import com.example.Ildeurim.dto.review.ReviewSummaryRes;
import com.example.Ildeurim.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    /*리뷰 추가*/
    @PostMapping
    public ResponseEntity<ApiResponse> createReview(@RequestParam Long workerId, @RequestBody ReviewCreateReq req){
        ReviewRes res = reviewService.createReview(workerId,req);
        return ResponseEntity.ok(new ApiResponse(true,201,"후기 작성 성공",res));
    }

    /*리뷰 조회*/
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getReview(@PathVariable Long employerId){
        ReviewSummaryRes res = reviewService.getReview(employerId);
        return ResponseEntity.ok(new ApiResponse(true,200,"후기 조회 성공",res));
    }
}
