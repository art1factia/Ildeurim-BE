package com.example.Ildeurim.commons.enums.review;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum EvaluationType {
    BOSS_KINDNESS("사장님 친절도"),
    COWORKER_KINDNESS("동료 친절도"),
    WORK_INTENSITY("업무 강도"),
    WORK_CLARITY("업무 명확성"),
    PAYDAY_COMPLIANCE("급여일 준수"),
    PAYMENT("시급 수준"),
    RESTTIME_COMPLIANCE("휴게시간 준수"),
    WELFARE_LEVEL("복지 수준"),
    REEMPLOYMENT_INTENTION("재근무 의사");

    private final String label;

    EvaluationType(String label) {
        this.label = label;
    }

    @JsonValue // JSON 직렬화 시 label 값으로 나가게 됨
    public String getLabel() {
        return label;
    }

    public static EvaluationType fromLabel(String label) {
        return Arrays.stream(values())
                .filter(e -> e.label.equals(label))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown EvaluationType label: " + label));
    }
}
