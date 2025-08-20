package com.example.Ildeurim.domain.quickAnswer;

import java.util.List;

public record QuestionList(List<QuestionItem> items) {
    public static QuestionList empty(){
        return  new QuestionList(List.of());
    }
}
