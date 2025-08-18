package com.example.Ildeurim.commons.enums.jobpost;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentType {
    HOURLY("시급"),
    DAILY("일급"),
    MONTHLY("월급"),
    PER_TASK("건당");


    private final String label;

    PaymentType(String label) {
        this.label = label;
    }

    @JsonValue // JSON 직렬화 시 label 값이 반환되도록 함
    public String getLabel() {
        return label;
    }
    @JsonCreator
    public static PaymentType fromLabel(String label) {
        for (PaymentType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PaymentType label: " + label);
    }
}
