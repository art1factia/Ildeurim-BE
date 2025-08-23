package com.example.Ildeurim.dto.review;

import com.example.Ildeurim.commons.enums.review.EvaluationAnswer;
import com.example.Ildeurim.commons.enums.review.EvaluationType;
import com.example.Ildeurim.commons.enums.review.Hashtag;
import jakarta.validation.constraints.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;
import java.util.Map;


public record ReviewCreateReq(
        @NotNull Long employerId,
        @NotNull Map<EvaluationType, EvaluationAnswer> answers,
        List<Hashtag> hashtags
) { }
