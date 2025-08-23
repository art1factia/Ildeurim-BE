package com.example.Ildeurim.domain.quickAnswer;
import java.util.ArrayList;
import java.util.List;

public record AnswerList(List<AnswerItem> items) {
    public static AnswerList empty() {
        return new AnswerList(List.of());
    }

    public AnswerList addItem(AnswerItem newItem) {
        List<AnswerItem> newItems = new ArrayList<>(this.items);
        newItems.removeIf(item -> item.questionId().equals(newItem.questionId()));
        newItems.add(newItem);
        return new AnswerList(newItems);
    }
}
