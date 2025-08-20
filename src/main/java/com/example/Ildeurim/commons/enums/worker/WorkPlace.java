package com.example.Ildeurim.commons.enums.worker;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WorkPlace {

    JONGRO("종로"),
    JUNG("중구"),
    YONGSAN("용산"),
    SEONGDONG("성동"),
    GWANGJIN("광진"),
    DONGDAEMUN("동대문"),
    JUNGRANG("중랑"),
    SEONGBUK("성북"),
    GANGBUK("강북"),
    DOBONG("도봉"),
    NOWON("노원"),
    EUNPYEONG("은평"),
    SEODAEMUN("서대문"),
    MAPO("마포"),
    YANGCHEON("양천"),
    GANGSEO("강서"),
    GURO("구로"),
    GEUMCHEON("금천"),
    YEONGDEUNGPO("영등포"),
    DONGJAK("동작"),
    GWANAK("관악"),
    SEOCHO("서초"),
    GANGNAM("강남"),
    SONGPA("송파"),
    GANGDONG("강동");


    private final String label;

    WorkPlace(String label) {
        this.label = label;
    }

    @JsonValue // JSON 직렬화 시 label 값이 반환되도록 함
    public String getLabel() {
        return label;
    }
}

