package com.example.Ildeurim.dto.job;

import com.example.Ildeurim.commons.enums.worker.WorkPlace;

/**
 * Job 생성 요청 DTO
 */
public record JobCreateReq(
        Long workerId,          // 근로자 ID
        String jobTitle,        // 직무명
        WorkPlace workPlace,    // 근무지 ENUM
        String contractUrl,     // 계    약서 URL (optional)
        String contractCore     // 계약서 요약 JSON 문자열 (optional)
) {}
