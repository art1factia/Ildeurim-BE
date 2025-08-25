package com.example.Ildeurim.gpt;

// FilterPrompts.java
public class FilterPrompts {
    public static String SYSTEM = """
            역할: 한국어 자연어 구인검색 문장을 엄격한 enum/수치 기반의 필터 JSON으로 변환합니다.
            - 반드시 '허용 enum의 한글 label'만 출력(철자 정확).
            - 지명(도봉구, 강서구 등)은 우선 workPlace(enum 한글 label: "도봉", "강서" 등)로 매핑하고, 불명확하면 locationContains에 넣으세요.
            - 날짜/금액 단위는 숫자로 환산, 요일/횟수 규칙은 기존과 동일.
            - 해석 불가 정보는 keywords에 보존. 추측 금지.
            """;

    public static String userPrompt(String queryKo, String allowedCatalog) {
        return """
                사용자 입력:
                "%s"
                
                %s
                
                출력(JSON 스키마):
                {
                  "jobField": ["..."],            // 한글 라벨만 허용
                  "workPlace": ["..."],           // 한글 라벨만 허용 (가능하면 여기 먼저)
                  "locationContains": ["..."],    // 매핑이 안 된 지명은 문자열로
                  "paymentType": ["..."],         // 한글 라벨만 허용
                  "paymentMin": 0,
                  "paymentMax": null,
                  "workType": "...",              // 한글 라벨 또는 null만 허용
                  "workDays": ["..."],            // 한글 라벨만 허용
                  "workDaysCountMin": null,
                  "workDaysCountMax": null,
                  "employmentType": ["..."],      // 한글 라벨만 허용
                  "applyMethods": ["..."],        // 한글 라벨만 허용
                  "keywords": ["원문 보존 키워드들"]
                }
                
                반드시 유효한 JSON만 출력.
                """.formatted(queryKo, allowedCatalog);
    }
}
