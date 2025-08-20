package com.example.Ildeurim.domain.quickAnswer;

public record AnswerItem(
        String questionId,        // 연결된 QuestionItem.id
        String text,              // 주관식 답변
        String optionIds    // 객관식 선택 항목 id
) {
}
