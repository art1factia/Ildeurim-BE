package com.example.Ildeurim.commons.enums.jobpost;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WorkDays {
    MONDAY("월요일"),
    TUESDAY("화요일"),
    WEDNESDAY("수요일"),
    THURSDAY("목요일"),
    FRIDAY("금요일"),
    SATURDAY("토요일"),
    SUNDAY("일요일");


    private final String label;

    WorkDays(String label) {
        this.label = label;
    }

    @JsonValue // JSON 직렬화 시 label 값이 반환되도록 함
    public String getLabel() {
        return label;
    }
    @JsonCreator
    public static WorkDays fromLabel(String label) {
        for (WorkDays days : values()) {
            if (days.label.equals(label)) {
                return days;
            }
        }
        throw new IllegalArgumentException("Unknown PaymentType label: " + label);
    }
    @JsonCreator
    public static WorkDays fromLabelNullable(String label) {
        for (WorkDays workDays : values()) {
            if (workDays.label.equals(label)) {
                return workDays;
            }
        }
        return null;
    }
}
