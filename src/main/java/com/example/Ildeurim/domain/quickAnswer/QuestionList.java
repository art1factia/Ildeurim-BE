package com.example.Ildeurim.domain.quickAnswer;

import com.example.Ildeurim.commons.enums.QuestionType;

import java.util.List;

public record QuestionList(List<QuestionItem> items) {
    public static QuestionList empty(){
        return  new QuestionList(List.of());
    }
}