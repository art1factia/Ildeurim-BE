package com.example.Ildeurim.dto.review;

import com.example.Ildeurim.commons.enums.review.EvaluationAnswer;
import com.example.Ildeurim.commons.enums.review.EvaluationType;
import com.example.Ildeurim.commons.enums.review.Hashtag;
import jakarta.validation.constraints.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;
import java.util.Map;

/**
 * 리뷰 생성 요청 DTO
 * - 로그인 중인 workerId는 DTO에 포함하지 않고 SecurityContext에서 읽음.
 * - EMPLOYER에 대한 리뷰만 허용
 */
public record ReviewCreateReq(
        @NotNull Long employerId,
        @NotNull Map<EvaluationType, EvaluationAnswer> answers,
        List<Hashtag> hashtags
) { }
