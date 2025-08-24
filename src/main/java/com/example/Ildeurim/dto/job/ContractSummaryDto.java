package com.example.Ildeurim.dto.job;

import com.fasterxml.jackson.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ContractSummaryDto {
    public String title;                        // 계약서 제목(있으면)
    public Parties parties;
    public Period period;
    public Payment payment;
    public List<String> obligations;            // 주요 의무
    public List<String> restrictions;           // 금지/제한
    public List<String> termination;            // 해지 조항 핵심
    public List<String> confidentiality;        // 비밀유지 요점
    public String governingLaw;                 // 준거법
    public List<String> risks;                  // 리스크 포인트(요약)
    public String executiveSummaryKo;           // 한국어 요약 5~8문장
    public String extractionConfidence;         // "high|medium|low"

    public static class Parties {
        public Optional<String> partyA;
        public Optional<String> partyB;
        public Optional<String> partyAType; // 회사/개인 등
        public Optional<String> partyBType;
    }
    public static class Period {
        public Optional<LocalDate> effectiveDate;
        public Optional<LocalDate> endDate;
        public Optional<Boolean> autoRenewal;
        public Optional<String> renewalTerm; // "1 year" 등 자연어
    }
    public static class Payment {
        public Optional<String> currency;
        public Optional<Double> totalAmount;
        public Optional<String> schedule; // 분할/마일스톤 설명
    }
}
