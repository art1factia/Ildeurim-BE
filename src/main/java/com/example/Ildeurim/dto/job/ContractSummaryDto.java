package com.example.Ildeurim.dto.job;

import java.util.List;

public record ContractSummaryDto(
        String abstraction, //한눈에보기,          // 3~4줄 요약
        String duration, //계약기간,            // 예: "2025-01-01 ~ 2025-12-31(자동연장 없음)"
        String workTimeInfo, //일하는시간과요일,    // 예: "월~금 09:00-18:00(휴게 1시간)"
        String paymentType, //급여와지급방식,      // 예: "월급 200만원, 매월 25일"
        String workLocation, //근무장소,            // 예: "서울 도봉구 OO로 12"
        List<String> notice,//꼭알아두기,    // 5~8개, 쉬운 문장
        List<String> caution,//주의할점,      // 분쟁/위약금/비밀유지 등 핵심 경고
        String contact//문의방법;            // 전화/이메일 등 간단히
) {

}