package com.example.Ildeurim.commons.enums.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum ApplicationStatus {

        DRAFT("임시저장"),
        NEEDINTERVIEW("면접 전"),
        PENDING("보류"),
        ACCEPTED("승인"),
        REJECTED("거부"),
        HIRED("고용");

    private final String label;

    ApplicationStatus(String label) {
        this.label = label;
    }

    @JsonValue // JSON 직렬화 시 label 값이 반환되도록 함
    public String getLabel() {
        return label;
    }
    @JsonCreator
    public static ApplicationStatus fromLabel(String label) {
        for (ApplicationStatus applicationStatus : values()) {
            if (applicationStatus.label.equals(label)) {
                return applicationStatus;
            }
        }
        throw new IllegalArgumentException("Unknown ApplicationStatus label: " + label);
    }

    public static ApplicationStatus fromString(String value) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid apply method: " + value));
    }
}
