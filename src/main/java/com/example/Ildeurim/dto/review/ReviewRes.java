package com.example.Ildeurim.dto.review;

import com.example.Ildeurim.commons.enums.review.EvaluationAnswer;
import com.example.Ildeurim.commons.enums.review.EvaluationType;
import com.example.Ildeurim.commons.enums.review.Hashtag;
import com.example.Ildeurim.domain.Review;
import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewRes {

    private Long id;
    private Long employerId;
    private Map<EvaluationType, EvaluationAnswer> answers;
    private Set<Hashtag> hashtags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReviewRes of(Review review) {
        return ReviewRes.builder()
                .id(review.getId())
                .employerId(review.getEmployer().getId())
                .answers(review.getAnswers())
                .hashtags(review.getHashtags())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}