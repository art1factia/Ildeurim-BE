package com.example.Ildeurim.commons.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EmploymentType {
    FULL_TIME("정규직"),
    PART_TIME("파트타임"),
    TEMPORARY("계약직");

    private final String label;

    EmploymentType(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static EmploymentType fromLabel(String label) {
        for (EmploymentType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown EmploymentType label: " + label);
    }
}