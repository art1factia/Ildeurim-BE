package com.example.Ildeurim.dto.application.req;

import com.example.Ildeurim.domain.quickAnswer.AnswerItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApplicationAnswerUpdateReq(
        @NotNull Boolean isCareerIncluding,
        @NotNull AnswerItem answer        // 질문 답변
) {
}