package com.example.Ildeurim.controller;

import com.example.Ildeurim.dto.ApiResponse;
import com.example.Ildeurim.dto.career.CareerCreateReq;
import com.example.Ildeurim.dto.career.CareerRes;
import com.example.Ildeurim.dto.career.CareerUpdateReq;
import com.example.Ildeurim.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/careers")
public class CareerController {

    private final CareerService careerService;

    /* create career */
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody CareerCreateReq req) {
        CareerRes res = careerService.create(req);
        // 팀 스타일: 항상 ok(...)로 감싸고, 바디 code 필드로 201 표기
        return ok(new ApiResponse(true, 201, "create career success", res));
    }

    /* update career */
    @PatchMapping("/{careerId}")
    public ResponseEntity<ApiResponse> update(
            @PathVariable Long careerId,
            @RequestBody CareerUpdateReq req
    ) {
        CareerRes res = careerService.update(careerId, req);
        return ok(new ApiResponse(true, 200, "update career success", res));
    }

    /* delete career */
    @DeleteMapping("/{careerId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long careerId) {
        careerService.delete(careerId);
        // 삭제는 payload 없이 메시지만
        return ok(new ApiResponse(true, 200, "delete career success"));
    }
}
