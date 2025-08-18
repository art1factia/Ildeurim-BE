package com.example.Ildeurim.dto.review;

import com.example.Ildeurim.commons.enums.review.Hashtag;
import com.example.Ildeurim.domain.Review;
import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewRes {

    private Long id;

    // 리뷰 대상 employerId
    private Long targetId;

    // 본문
    private Integer rating;        // 1~5
    private String content;
    private List<Hashtag> hashtags; // @JsonValue 설정되어 있다면 라벨로 직렬화됨

    // 작성자/감사 정보(필요하면 노출)
    private Long workerId;         // 로그인한 작성자 id
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    /**
     * 엔티티 → DTO 변환 헬퍼 (편의용)
     */
    public static ReviewRes from(Review review) {
        return ReviewRes.builder()
                .id(review.getId())
                .targetId(review.getEmployer().getId())
                .rating(review.getRating())
                .content(review.getContent())
                .hashtags(review.getHashtags())
                .workerId(review.getWorker().getId())
                .createdAt(review.getCreatedAt() == null ? null : review.getCreatedAt().atOffset(ZoneOffset.UTC))
                .updatedAt(review.getUpdatedAt() == null ? null : review.getUpdatedAt().atOffset(ZoneOffset.UTC))
                .build();
    }
}