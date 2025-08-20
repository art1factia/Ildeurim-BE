package com.example.Ildeurim.domain.quickAnswer;

import com.example.Ildeurim.commons.enums.QuestionType;

import java.util.List;

public record QuestionItem(
        String id,                // 식별자
        String text,              // 질문 본문
        QuestionType type,              // 선택 유형
        List<String> options      // 선택지 (단답형이면 null/빈 배열)
) {
}