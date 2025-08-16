package com.example.Ildeurim.commons.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Hashtag {

    // 긍정적
    FRIENDLY_ATMOSPHERE("친절한 분위기"),
    CLEAN_ENVIRONMENT("청결한 환경"),
    GOOD_COMMUNICATION("원활한 소통"),
    FAIR_WAGE("합리적인 급여"),
    FLEXIBLE_SCHEDULE("유연한 근무시간"),
    SUPPORTIVE_MANAGEMENT("배려 깊은 경영진"),
    SKILL_IMPROVEMENT("역량 향상 기회"),
    GOOD_TEAMWORK("팀워크가 좋음"),
    POSITIVE_FEEDBACK("긍정적인 피드백"),
    STABLE_WORK("안정적인 업무"),

    // 중립적
    REGULAR_TASKS("규칙적인 업무"),
    AVERAGE_PAY("평균적인 급여"),
    BASIC_TRAINING("기본 교육 제공"),
    NORMAL_WORKLOAD("평범한 업무량"),
    FIXED_SCHEDULE("정해진 근무시간"),
    STANDARD_RULES("표준 규정 준수"),
    CLEAR_INSTRUCTIONS("명확한 지시"),
    SIMPLE_TASKS("단순한 업무"),
    ADEQUATE_RESOURCES("충분한 자원"),
    AVERAGE_ENVIRONMENT("보통의 환경"),

    // 부정적
    LONG_HOURS("긴 근무시간"),
    LOW_PAY("낮은 급여"),
    POOR_MANAGEMENT("미흡한 경영"),
    LACK_OF_COMMUNICATION("소통 부족"),
    UNSAFE_ENVIRONMENT("위험한 작업환경"),
    HIGH_STRESS("높은 업무강도"),
    LACK_OF_SUPPORT("지원 부족"),
    UNFAIR_TREATMENT("불공정한 대우"),
    LACK_OF_RESOURCES("부족한 자원"),
    MONOTONOUS_WORK("단조로운 업무");

    private final String label;

    Hashtag(String label) {
        this.label = label;
    }

    @JsonValue // JSON 직렬화 시 label 값이 반환되도록 함
    public String getLabel() {
        return label;
    }
}
