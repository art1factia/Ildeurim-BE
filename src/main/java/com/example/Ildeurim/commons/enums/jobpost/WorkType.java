package com.example.Ildeurim.commons.enums.jobpost;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WorkType {
    WEEKLY("주 횟수"),
    SPECIFY("요일 지정");


    private final String label;

    WorkType(String label) {
        this.label = label;
    }

    @JsonValue // JSON 직렬화 시 label 값이 반환되도록 함
    public String getLabel() {
        return label;
    }
    @JsonCreator
    public static WorkType fromLabel(String label) {
        for (WorkType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PaymentType label: " + label);
    }
    @JsonCreator
    public static WorkType fromLabelNullable(String label) {
        for (WorkType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        return null;
    }
}
