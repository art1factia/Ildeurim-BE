package com.example.Ildeurim.dto.review;

import com.example.Ildeurim.commons.enums.review.EvaluationAnswer;
import com.example.Ildeurim.commons.enums.review.EvaluationType;
import com.example.Ildeurim.commons.enums.review.Hashtag;
import jakarta.validation.constraints.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public record ReviewCreateReq(
        @NotNull Long jobId,
        @NotNull Long employerId,
        @NotNull Map<String, Integer> answers,
        List<String> hashtags
) {
    public Map<EvaluationType, EvaluationAnswer> toAnswerEnums() {
        //모든 항목이 채워졌는지 확인
        if (!Arrays.stream(EvaluationType.values())
                .allMatch(e -> answers.containsKey(e.getLabel()))) {
            throw new IllegalArgumentException("모든 평가 항목을 작성해야 합니다.");
        }


        return answers.entrySet().stream()
            .peek(e -> { // 존재하지 않는 label이면 예외 발생
                if (!isValidEvaluationType(e.getKey())) {
                    throw new IllegalArgumentException("알 수 없는 평가 항목: " + e.getKey());
                }
            })
            .collect(Collectors.toMap(
                    e -> EvaluationType.fromLabel(e.getKey()),
                    e -> EvaluationAnswer.fromCode(e.getValue())
            ));
}

    public List<Hashtag> toHashtagEnums() {
        if (hashtags == null) return List.of();
        return hashtags.stream()
                .peek(h -> { // 존재하지 않는 label이면 예외 발생
                    if (!isValidHashtag(h)) {
                        throw new IllegalArgumentException("알 수 없는 해시태그: " + h);
                    }
                })
                .map(Hashtag::fromLabel)
                .collect(Collectors.toList());
    }

    private boolean isValidEvaluationType(String label) {
        return java.util.Arrays.stream(EvaluationType.values())
                .anyMatch(e -> e.getLabel().equals(label));
    }

    private boolean isValidHashtag(String label) {
        return java.util.Arrays.stream(Hashtag.values())
                .anyMatch(h -> h.getLabel().equals(label));
    }

}


