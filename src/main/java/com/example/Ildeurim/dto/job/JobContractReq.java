package com.example.Ildeurim.dto.job;

/**
 * 계약서 등록/수정 요청 DTO
 */
public record JobContractReq(
        String contractUrl,     // 계약서 파일 URL
        String contractCore     // 계약서 핵심 내용(JSON 문자열)
) {}
