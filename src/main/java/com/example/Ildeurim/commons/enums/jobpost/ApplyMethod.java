package com.example.Ildeurim.commons.enums.jobpost;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

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

    @JsonCreator
    public static ApplyMethod fromLabelNullable(String label) {
        for (ApplyMethod method : values()) {
            if (method.label.equals(label)) {
                return method;
            }
        }
        return null;
    }

    public static ApplyMethod fromString(String value) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid apply method: " + value));
    }
}
