package com.example.Ildeurim.commons.enums.jobpost;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum JobPostStatus{
    OPEN("모집 중"),
    CLOSE("모집 마감");

    private final String label;

    JobPostStatus(String label) {
        this.label = label;
    }

    @JsonValue // JSON 직렬화 시 label 값이 반환되도록 함
    public String getLabel() {
        return label;
    }
    @JsonCreator
    public static JobPostStatus fromLabel(String label) {
        for (JobPostStatus status : values()) {
            if (status.label.equals(label)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown JobpostStatus label: " + label);
    }
}

