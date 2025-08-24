package com.example.Ildeurim.dto.job;

import com.example.Ildeurim.commons.enums.worker.WorkPlace;

/**
 * Job 생성 요청 DTO
 */
public record JobCreateReq(
        Long workerId,          // 근로자 ID
        Long applicationId,
        String jobTitle,        // 직무명
        String workPlace    // 근무지 ENUM
) {}
