package com.example.Ildeurim.gpt;

// FilterNormalizer.java
import com.example.Ildeurim.commons.enums.jobpost.WorkType;
import com.example.Ildeurim.commons.enums.worker.WorkPlace; // ← 추가
import com.example.Ildeurim.dto.jobpost.JobPostFilter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterNormalizer {

    public static java.util.LinkedHashSet<WorkPlace> ensurePreferred(JobPostFilter f) {
        var preferred = new java.util.LinkedHashSet<WorkPlace>();
        if (f.workPlace != null) preferred.addAll(f.workPlace);

        if (preferred.isEmpty() && f.locationContains != null) {
            for (String w : f.locationContains) {
                for (WorkPlace wp : WorkPlace.values()) {
                    if (wp.getLabel().contains(w)) { // "마포" 포함시 선호로
                        preferred.add(wp);
                    }
                }
            }
        }
        return preferred;
    }


    /** "주 2일 / 월 3회" 같은 표현에서 정수 하나를 추출 (첫 번째 숫자) */
    public static Integer sniffCount(String s) {
        if (s == null) return null;
        Matcher m = Pattern.compile("(\\d{1,2})").matcher(s);
        if (m.find()) {
            try { return Integer.parseInt(m.group(1)); } catch (Exception ignore) {}
        }
        return null;
    }

    /**
     * 금액 문자열을 원(₩) 단위 Long으로 변환
     * 지원: "12000원", "1.2만원", "120만원", "1,200", "12000", "1만2천원" (간이)
     */
    public static Long toWon(String s) {
        if (s == null) return null;
        String t = s.replaceAll("\\s+", "");

        // "1만2천원" 같이 섞인 표현 간이 처리
        long won = 0L;
        boolean matched = false;

        Matcher man = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*만(?=원|원|$)").matcher(t);
        while (man.find()) {
            matched = true;
            won += Math.round(Double.parseDouble(man.group(1)) * 1_000_000L);
        }
        Matcher cheon = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*천(?=원|원|$)").matcher(t);
        while (cheon.find()) {
            matched = true;
            won += Math.round(Double.parseDouble(cheon.group(1)) * 1_000L);
        }
        // "…원" 독립 숫자
        Matcher wonNum = Pattern.compile("(\\d[\\d,]*)\\s*원").matcher(t);
        if (won == 0 && wonNum.find()) {
            matched = true;
            won = Long.parseLong(wonNum.group(1).replace(",", ""));
        }
        // 그냥 숫자만 있는 경우
        if (!matched) {
            Matcher only = Pattern.compile("\\b(\\d[\\d,]*)\\b").matcher(t);
            if (only.find()) {
                try { won = Long.parseLong(only.group(1).replace(",", "")); matched = true; } catch (Exception ignore) {}
            }
        }
        return matched ? won : null;
    }

    /** "도봉구, 월요일, 시급" 같은 한글 문장 토큰화를 위한 간단 분해기 */
    private static Set<String> tokensFrom(String raw) {
        if (raw == null) return new LinkedHashSet<>();
        // 쉼표/슬래시/점/스페이스 기준 분해 (+ 한글 조사를 어느 정도 분리하기 위해 구두점 포함)
        String[] arr = raw.split("[,·/\\s]+");
        Set<String> set = new LinkedHashSet<>();
        for (String a : arr) {
            String s = a.trim();
            if (!s.isEmpty()) set.add(s);
        }
        return set;
    }

    /**
     * LLM이 만든 1차 필터(JSON)를 받아 서버 기준으로 최종 보정.
     * - EnumCatalog.*resolve*(…) 들은 "한국어 동의어 → Enum" 매퍼(서버 결정권)
     * - WorkPlace: 자연어 지명 → enum 매핑 우선, 실패 시 locationContains 유지
     */
    public static JobPostFilter normalize(JobPostFilter in, String rawQuery) {
        JobPostFilter out = new JobPostFilter();

        // ===== 0) 널/기본값 방어 =====
        out.jobField        = safeSet(in != null ? in.jobField        : null);
        out.paymentType     = safeSet(in != null ? in.paymentType     : null);
        out.workDays        = safeSet(in != null ? in.workDays        : null);
        out.employmentType  = safeSet(in != null ? in.employmentType  : null);
        out.applyMethods    = safeSet(in != null ? in.applyMethods    : null);
        out.locationContains= safeStrSet(in != null ? in.locationContains : null);
        out.keywords        = safeStrSet(in != null ? in.keywords        : null);
        out.workPlace       = safeSet(in != null ? in.workPlace       : null);

        out.workType        = (in != null ? in.workType       : null);
        out.workDaysCountMin= (in != null ? in.workDaysCountMin: null);
        out.workDaysCountMax= (in != null ? in.workDaysCountMax: null);
        out.paymentMin      = (in != null ? in.paymentMin     : null);
        out.paymentMax      = (in != null ? in.paymentMax     : null);

        // ===== 1) 원문 토큰 추출 =====
        Set<String> tokens = tokensFrom(rawQuery);

        // ===== 2) 기존 보정: jobField / workDays / paymentType =====
        // LLM 누락 보완(서버 사전 기반)
        out.jobField.addAll(EnumCatalog.resolveJobFields(tokens));
        out.workDays.addAll(EnumCatalog.resolveWorkDays(tokens));
        out.paymentType.addAll(EnumCatalog.resolvePaymentTypes(tokens));

        // ===== 3) WorkPlace 보정 =====
        // 3-1) 원문 토큰에서 WorkPlace 시도
        out.workPlace.addAll(EnumCatalog.resolveWorkPlace(tokens));

        // 3-2) LLM이 locationContains에 넣어 준 지명 재해석 → WorkPlace

        // 3-3) 여전히 WorkPlace가 비어 있다면, locationContains는 그대로 유지하여 LIKE 검색에 사용
        //      (WorkPlace가 채워졌더라도 locationContains는 보조 검색용으로 남겨둘 수 있음)

        // ===== 4) workType 보정 =====
        // 규칙:
        // - 요일이 하나라도 있으면 SPECIFY
        // - 숫자만 발견되면 WEEKLY + count
        if (out.workType == null) {
            if (!out.workDays.isEmpty()) {
                out.workType = WorkType.SPECIFY;
            } else {
                Integer c = sniffCount(rawQuery);
                if (c != null) {
                    out.workType = WorkType.WEEKLY;
                    out.workDaysCountMin = (out.workDaysCountMin == null) ? c : out.workDaysCountMin;
                    out.workDaysCountMax = (out.workDaysCountMax == null) ? c : out.workDaysCountMax;
                }
            }
        } else {
            // SPECIFY인데 요일이 비었으면 원문에서 재시도
            if (out.workType == WorkType.SPECIFY && out.workDays.isEmpty()) {
                out.workDays.addAll(EnumCatalog.resolveWorkDays(tokens));
            }
            // WEEKLY인데 count가 없으면 원문에서 수 추출
            if (out.workType == WorkType.WEEKLY && out.workDaysCountMin == null && out.workDaysCountMax == null) {
                Integer c = sniffCount(rawQuery);
                if (c != null) {
                    out.workDaysCountMin = c;
                    out.workDaysCountMax = c;
                }
            }
        }

        // ===== 5) 금액 보정 (이상/이하 뉘앙스 반영) =====
        // - "이상/최소"가 들어가면 paymentMin 우선
        // - "이하/최대/까지"가 들어가면 paymentMax 우선
        // - 둘 다 없고 값도 비어있으면 rawQuery에서 숫자 하나 뽑아 Min으로
        String q = (rawQuery == null ? "" : rawQuery);
        if (out.paymentMin == null || out.paymentMax == null) {
            Long wonInQuery = toWon(q);
            if (wonInQuery != null) {
                boolean ge = q.contains("이상") || q.contains("최소") || q.toLowerCase().contains("over") || q.toLowerCase().contains("min");
                boolean le = q.contains("이하") || q.contains("최대") || q.contains("까지") || q.toLowerCase().contains("under") || q.toLowerCase().contains("max");
                if (ge && out.paymentMin == null) out.paymentMin = wonInQuery;
                else if (le && out.paymentMax == null) out.paymentMax = wonInQuery;
                else if (out.paymentMin == null && out.paymentMax == null) out.paymentMin = wonInQuery;
            }
        }
        // 경계 역전 방지
        if (out.paymentMin != null && out.paymentMax != null && out.paymentMin > out.paymentMax) {
            Long tmp = out.paymentMin; out.paymentMin = out.paymentMax; out.paymentMax = tmp;
        }

        return out;
    }

    // ====== helpers ======
    private static <E> Set<E> safeSet(Set<E> s) {
        return (s == null) ? new LinkedHashSet<>() : new LinkedHashSet<>(s);
    }
    private static Set<String> safeStrSet(Set<String> s) {
        return (s == null) ? new LinkedHashSet<>() : new LinkedHashSet<>(s);
    }
}
