package com.example.Ildeurim.dto.application;

import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import com.example.Ildeurim.domain.quickAnswer.AnswerList;

public record ApplicationUpdateDto(
        Long applicationId,         // 이미 생성된 지원서 ID
        AnswerList answers,         // 질문 답변 리스트
        ApplicationStatus status    // DRAFT / PENDING / NEED_INTERVIEW ...
) {
}