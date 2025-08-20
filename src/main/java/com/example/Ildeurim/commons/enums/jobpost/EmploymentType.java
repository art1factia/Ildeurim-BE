package com.example.Ildeurim.commons.enums.jobpost;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EmploymentType {
    PART_TIME("아르바이트"),      // 아르바이트
    FULL_TIME("정규직"),      // 정규직
    TEMPORARY("계약직"),       // 계약직
    DISPATCH("파견직"),       // 파견직
    INTERN("인턴직"),         // 인턴직
    TRAINEE("교육생"),        // 교육생
    FREELANCER("프리랜서");      // 프리랜서;

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