package com.example.Ildeurim.dto.review;

import com.example.Ildeurim.commons.enums.Hashtag;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;

/**
 * 리뷰 업데이트 요청 DTO
 * - 모든 필드는 optional
 * - null이 아닌 값만 서비스 계층에서 업데이트 적용
 */
@Builder
public record ReviewUpdateReq(

        @Nullable
        @Min(1) @Max(5)
        Integer rating, // 별점 (1~5)

        @Nullable
        String content, // 리뷰 내용

        @Nullable
        List<Hashtag> hashtag // 해시태그 목록 (최대 N개)
) {
}