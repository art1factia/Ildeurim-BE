package com.example.Ildeurim.domain.quickAnswer;
import java.util.List;

public record AnswerList(List<AnswerItem> items) {
    public static AnswerList empty() {
        return new AnswerList(List.of());
    }
}
