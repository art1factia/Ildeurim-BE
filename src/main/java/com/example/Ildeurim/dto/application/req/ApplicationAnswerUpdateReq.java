package com.example.Ildeurim.dto.application.req;

import com.example.Ildeurim.domain.quickAnswer.AnswerItem;

public record ApplicationAnswerUpdateReq(
        Long applicationId,         // 이미 생성된 지원서 ID
        AnswerItem answer        // 질문 답변
) {
}