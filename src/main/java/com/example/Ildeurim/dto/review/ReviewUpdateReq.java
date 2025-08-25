package com.example.Ildeurim.dto.review;

import com.example.Ildeurim.commons.enums.review.EvaluationAnswer;
import com.example.Ildeurim.commons.enums.review.EvaluationType;
import com.example.Ildeurim.commons.enums.review.Hashtag;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;
import java.util.Map;


@Builder
public record ReviewUpdateReq(
        @NotNull Map<EvaluationType, EvaluationAnswer> answers,
        List<Hashtag> hashtags
) {
}