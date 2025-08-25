package com.example.Ildeurim.commons.enums.review;

import com.example.Ildeurim.commons.enums.jobpost.ApplyMethod;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Hashtag {

    PROMOTION_OPPORTUNITY("승진 기회"),
    TRAINING_PROVIDED("교육 제공"),
    BULLYING("텃세"),
    STRICTNESS("엄격"),
    POOR_HYGIENE("위생이 안좋음"),
    FLEXIBLE_WORK("유연 근무"),
    SHIFT_WORK("교대 근무"),
    COMFORTABLE_ENVIRONMENT("근무환경 쾌적");

    private final String label;

    Hashtag(String label) {
        this.label = label;
    }

    @JsonValue // JSON 직렬화 시 label 값이 반환되도록 함
    public String getLabel() {
        return label;
    }
    @JsonCreator
    public static Hashtag fromLabel(String label) {
        for (Hashtag hashtag : values()) {
            if (hashtag.label.equals(label)) {
                return hashtag;
            }
        }
        throw new IllegalArgumentException("Unknown hashtag label: " + label);
    }
}
