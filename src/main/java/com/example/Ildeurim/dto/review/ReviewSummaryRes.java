package com.example.Ildeurim.dto.review;

import com.example.Ildeurim.commons.enums.review.EvaluationAnswer;
import com.example.Ildeurim.commons.enums.review.EvaluationType;
import com.example.Ildeurim.commons.enums.review.Hashtag;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewSummaryRes {

    private Long employerId; // 고용주 ID

    // 질문별 종합 평가 (GOOD/NORMAL/BAD)
    private Map<EvaluationType, EvaluationAnswer> averages;

    // 해시태그 카운트
    private Map<Hashtag, Long> hashtagCounts;

    // 전체 리뷰 개수
    private Long totalReviews;

    //
}