package com.example.Ildeurim.commons.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QuestionType {
    SUBJECTIVE("예/아니요"),
    OBJECTIVE("서술형");

    private final String label;

    QuestionType(String label) {
        this.label = label;
    }

    @JsonValue // JSON 직렬화 시 label로 나가도록
    public String getLabel() {
        return label;
    }

    @JsonCreator // JSON 역직렬화 시 label로부터 생성
    public static QuestionType fromLabel(String label) {
        for (QuestionType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown QuestionType label: " + label);
    }
}
