package com.example.Ildeurim.dto.job;

import com.example.Ildeurim.commons.enums.worker.WorkPlace;

/**
 * Job 수정 요청 DTO (부분 업데이트)
 */
public record JobUpdateReq(
        String jobTitle,        // 직무명 (변경 가능)
        WorkPlace workPlace,    // 근무지 ENUM (변경 가능)
        String contractUrl,     // 계약서 URL (변경 가능)
        String contractCore     // 계약서 요약 JSON 문자열 (변경 가능)
) {}
