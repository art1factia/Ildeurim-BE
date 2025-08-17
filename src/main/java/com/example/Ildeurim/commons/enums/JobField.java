package com.example.Ildeurim.commons.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum JobField {

    AGRICULTURE("농사,원예,어업"),
    MANUFACTURING("목공,공예,제조"),
    DELIVERY("운전,배달"),
    MECHANIC_REPAIR("기계, 수리"),
    CLEANING("청소, 미화"),
    CONSTRUCTION("건설, 시설 관리"),
    ELECTRONICS_REPAIR("전자 수리"),
    CARE("돌봄"),
    SALES("판매"),
    FOOD_SERVICE("음식,서비스"),
    CULTURE_RESEARCH("문화, 연구"),
    OFFICE_FINANCE("사무, 금융");

    private final String label;

    JobField(String label) {
        this.label = label;
    }

    @JsonValue // JSON 직렬화 시 label 값이 반환되도록 함
    public String getLabel() {
        return label;
    }
}

