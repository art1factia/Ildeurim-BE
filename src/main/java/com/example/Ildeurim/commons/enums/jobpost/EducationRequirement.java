package com.example.Ildeurim.commons.enums.jobpost;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EducationRequirement {
    NONE("무관"),
    HIGH_SCHOOL("고등학교 졸업 이상"),
    BACHELOR("학사 이상"),
    MASTER("석사 이상"),
    DOCTOR("박사 이상");

    private final String label;

    EducationRequirement(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static EducationRequirement fromLabel(String label) {
        for (EducationRequirement edu : values()) {
            if (edu.label.equals(label)) {
                return edu;
            }
        }
        throw new IllegalArgumentException("Unknown EducationRequirement label: " + label);
    }
}
