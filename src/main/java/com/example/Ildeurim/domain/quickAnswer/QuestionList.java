package com.example.Ildeurim.domain.quickAnswer;

import com.example.Ildeurim.commons.enums.QuestionType;
import com.example.Ildeurim.domain.quickAnswer.QuestionItem;

import java.util.ArrayList;
import java.util.List;

public record QuestionList(List<QuestionItem> items) {
    public static QuestionList empty(){
        return  new QuestionList(List.of());
    }
    public QuestionList addItem(QuestionItem newItem) {
        List<QuestionItem> newItems = new ArrayList<>(this.items);
        newItems.add(newItem);
        return new QuestionList(newItems);
    }
}