package com.example.Ildeurim.dto.review;

import com.example.Ildeurim.commons.enums.review.EvaluationAnswer;
import com.example.Ildeurim.commons.enums.review.EvaluationType;
import com.example.Ildeurim.commons.enums.review.Hashtag;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * 리뷰 업데이트 요청 DTO
 * - 모든 필드는 optional
 * - null이 아닌 값만 서비스 계층에서 업데이트 적용
 */
@Builder
public record ReviewUpdateReq(
        @NotNull Map<EvaluationType, EvaluationAnswer> answers,

        List<Hashtag> hashtags
) {
}