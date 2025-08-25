package com.example.Ildeurim.commons.enums.review;

import java.util.Arrays;

public enum EvaluationAnswer {
    BAD(1), NORMAL(2), GOOD(3);

    private final int code;
    EvaluationAnswer(int code) { this.code = code; }
    public int getCode() { return code; }

    public static EvaluationAnswer fromCode(int code) {
        return Arrays.stream(values())
                .filter(e -> e.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown code: " + code));
    }

    public static EvaluationAnswer fromAverage(double avg) {
        int rounded = (int) Math.round(avg); // 1.4 → 1, 2.6 → 3
        return fromCode(Math.max(1, Math.min(3, rounded))); // 범위 보정
    }
}

