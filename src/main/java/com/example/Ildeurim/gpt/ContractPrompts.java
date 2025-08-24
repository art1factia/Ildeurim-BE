package com.example.Ildeurim.gpt;

// ContractPrompts.java (일부)
public class ContractPrompts {
    public static final String SYSTEM =
            "역할: 당신은 시니어 분들이 이해하기 쉬운 말로 계약서 핵심을 정리하는 도우미입니다. " +
                    "어려운 법 조항은 쉬운 표현으로 바꾸고, 중요한 내용은 목록으로 또박또박 정리하세요. " +
                    "존재하지 않는 정보는 추측하지 않습니다.";

    public static String userPrompt(String plainText) {
        return """
다음은 계약서 내용(원문 또는 OCR)입니다.

[계약서 원문]
%s

요구 출력(JSON, 필드명은 한국어 그대로):
{
  "abstraction": "3~4줄 간단 요약",
  "duration": "YYYY-MM-DD ~ YYYY-MM-DD (자동연장 여부 명시 또는 '미기재')",
  "workTimeInfo": "예: 월~금 09:00-18:00, 휴게 1시간",
  "paymentType": "예: 월급 200만원, 25일 지급 / 시급 12,000원 등",
  "workLocation": "가능한 한 구/동 수준으로",
  "notice": ["중요 조항 5~8개 (쉬운 문장)"],
  "caution": ["분쟁·위약금·비밀유지 등 주의 포인트 3~6개"],
  "contact": "전화/이메일 등 간단히"
}

반드시 유효한 JSON만 출력하세요.
""".formatted(plainText);
    }
}
