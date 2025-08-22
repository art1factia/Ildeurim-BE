package com.example.Ildeurim.dto.application.req;

import com.example.Ildeurim.domain.quickAnswer.AnswerItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApplicationAnswerUpdateReq(
        @NotNull Long applicationId,         // 이미 생성된 지원서 ID
        @NotBlank AnswerItem answer        // 질문 답변
) {
}