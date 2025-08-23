package com.example.Ildeurim.commons.enums.worker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE("남성"),
    FEMALE("여성");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    @JsonValue // JSON 직렬화 시 label 값이 반환되도록 함
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static Gender fromLabel(String label) {
        for (Gender gender : values()) {
            if (gender.label.equals(label)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Unknown gender label: " + label);
    }

    @JsonCreator
    public static Gender fromLabelNullable(String label) {
        for (Gender g : values()) {
            if (g.label.equals(label)) {
                return g;
            }
        }
        return null;
    }
}

