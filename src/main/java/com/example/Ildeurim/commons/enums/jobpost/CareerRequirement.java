package com.example.Ildeurim.commons.enums.jobpost;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CareerRequirement {
    NONE("무관"),
    HAVING("있음");

    private final String label;

    CareerRequirement(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static CareerRequirement fromLabel(String label) {
        for (CareerRequirement career : values()) {
            if (career.label.equals(label)) {
                return career;
            }
        }
        throw new IllegalArgumentException("Unknown CareerRequirement label: " + label);
    }

}
