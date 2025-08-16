package com.example.Ildeurim.commons.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ApplyMethod {
    QUICK("간편지원"),
    PHONE("전화지원");

    private final String label;

    ApplyMethod(String label) {
        this.label = label;
    }

    @JsonValue // JSON 직렬화 시 label 값이 반환되도록 함
    public String getLabel() {
        return label;
    }
    @JsonCreator
    public static ApplyMethod fromLabel(String label) {
        for (ApplyMethod method : values()) {
            if (method.label.equals(label)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown ApplyMethod label: " + label);
    }
}
