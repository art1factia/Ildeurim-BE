package com.example.Ildeurim.gpt;

// EnumCatalog.java

import java.util.*;
import java.util.stream.Collectors;

import com.example.Ildeurim.commons.enums.jobpost.*;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;

public class EnumCatalog {

    // === JobField: 업로드 파일의 한국어 라벨을 기반으로 동의어 매핑 ===
    // 라벨 예: CLEANING("청소,미화")
    private static final Map<JobField, List<String>> JOBFIELD_SYNONYMS =
            Map.ofEntries(
                    Map.entry(JobField.AGRICULTURE, List.of("농사", "원예", "어업", "농업")),
                    Map.entry(JobField.MANUFACTURING, List.of("목공", "공예", "제조")),
                    Map.entry(JobField.DELIVERY, List.of("운전", "배달", "배송", "퀵")),
                    Map.entry(JobField.MECHANIC_REPAIR, List.of("기계", "금속", "수리", "설비")),
                    Map.entry(JobField.CLEANING, List.of("청소", "미화", "환경미화")),
                    Map.entry(JobField.CONSTRUCTION, List.of("건설", "시설관리", "현장")),
                    Map.entry(JobField.ELECTRONICS_REPAIR, List.of("전기", "전자", "전기수리", "전자수리")),
                    Map.entry(JobField.CARE, List.of("돌봄", "요양", "간병", "시니어케어")),
                    Map.entry(JobField.SALES, List.of("판매", "매장", "마트", "캐셔")),
                    Map.entry(JobField.FOOD_SERVICE, List.of("음식", "서빙", "주방", "조리", "식당")),
                    Map.entry(JobField.CULTURE_RESEARCH, List.of("문화", "연구", "기술", "IT")),
                    Map.entry(JobField.OFFICE_FINANCE, List.of("사무", "오피스", "회계", "금융", "행정")),
                    Map.entry(JobField.FOOD_CLOTHE_ENV, List.of("식품", "옷", "의류", "환경", "가공")),
                    Map.entry(JobField.OTHER, List.of("기타", "그외"))
            );

    // WorkDays
    private static final Map<String, WorkDays> KOREAN_DAY_TO_ENUM = Map.ofEntries(
            Map.entry("월", WorkDays.MONDAY), Map.entry("월요일", WorkDays.MONDAY),
            Map.entry("화", WorkDays.TUESDAY), Map.entry("화요일", WorkDays.TUESDAY),
            Map.entry("수", WorkDays.WEDNESDAY), Map.entry("수요일", WorkDays.WEDNESDAY),
            Map.entry("목", WorkDays.THURSDAY), Map.entry("목요일", WorkDays.THURSDAY),
            Map.entry("금", WorkDays.FRIDAY), Map.entry("금요일", WorkDays.FRIDAY),
            Map.entry("토", WorkDays.SATURDAY), Map.entry("토요일", WorkDays.SATURDAY),
            Map.entry("일", WorkDays.SUNDAY), Map.entry("일요일", WorkDays.SUNDAY)
    );

    // PaymentType
    private static final Map<String, PaymentType> PAYTYPE_KO = Map.of(
            "시급", PaymentType.HOURLY,
            "일급", PaymentType.DAILY,
            "월급", PaymentType.MONTHLY,
            "건당", PaymentType.PER_TASK
    );

    public static Set<JobField> resolveJobFields(Collection<String> tokens) {
        Set<JobField> out = new LinkedHashSet<>();
        for (String tk : tokens) {
            String s = tk.trim();
            for (var e : JOBFIELD_SYNONYMS.entrySet()) {
                if (e.getValue().stream().anyMatch(s::contains)) out.add(e.getKey());
            }
            // enum 명 직접 입력(CLEANING 등)도 허용
            try {
                out.add(JobField.valueOf(s));
            } catch (Exception ignore) {
            }
        }
        return out;
    }

    public static Set<WorkDays> resolveWorkDays(Collection<String> tokens) {
        Set<WorkDays> out = new LinkedHashSet<>();
        for (String tk : tokens) {
            String s = tk.trim();
            if (KOREAN_DAY_TO_ENUM.containsKey(s)) out.add(KOREAN_DAY_TO_ENUM.get(s));
            // “월/수/금” 같이 붙어온 경우 분해
            for (String ch : s.split("[,·/\\s]+")) {
                if (KOREAN_DAY_TO_ENUM.containsKey(ch)) out.add(KOREAN_DAY_TO_ENUM.get(ch));
            }
            try {
                out.add(WorkDays.valueOf(s));
            } catch (Exception ignore) {
            }
        }
        return out;
    }

    public static Set<PaymentType> resolvePaymentTypes(Collection<String> tokens) {
        Set<PaymentType> out = new LinkedHashSet<>();
        for (String tk : tokens) {
            String s = tk.trim();
            if (PAYTYPE_KO.containsKey(s)) out.add(PAYTYPE_KO.get(s));
            try {
                out.add(PaymentType.valueOf(s));
            } catch (Exception ignore) {
            }
        }
        return out;
    }

    public static String allowedCatalogForPrompt() {
        String jobFieldLabels = Arrays.stream(JobField.values())
                .map(JobField::getLabel)
                .collect(Collectors.joining("/ "));
//        System.out.println(jobFieldLabels);

        String workPlaceLabels = Arrays.stream(WorkPlace.values())
                .map(WorkPlace::getLabel)
                .collect(Collectors.joining("/ "));

        String workTypeLabels = Arrays.stream(WorkType.values())
                .map(WorkType::getLabel)
                .collect(Collectors.joining("/ "));

        String workDaysLabels = Arrays.stream(WorkDays.values())
                .map(WorkDays::getLabel)
                .collect(Collectors.joining("/ "));

        String paymentTypeLabels = Arrays.stream(PaymentType.values())
                .map(PaymentType::getLabel)
                .collect(Collectors.joining("/ "));

        String employmentTypeLabels = Arrays.stream(EmploymentType.values())
                .map(EmploymentType::getLabel)
                .collect(Collectors.joining("/ "));

        String educationReqLabels = Arrays.stream(EducationRequirement.values())
                .map(EducationRequirement::getLabel)
                .collect(Collectors.joining("/ "));

        String applyMethodLabels = Arrays.stream(ApplyMethod.values())
                .map(ApplyMethod::getLabel)
                .collect(Collectors.joining("/ "));

        return """
                [허용 '라벨' 목록(정확히 아래 라벨만 출력)]
                
                
                - jobField(라벨):
                %s
                - workPlace(라벨):
                %s
                - workType(라벨):
                %s
                - workDays(라벨):
                %s
                - paymentType(라벨):
                %s
                - employmentType(라벨):
                %s
                - educationRequirement(라벨):
                %s
                - applyMethods(라벨):
                %s
                
                규칙:
                1) enum '이름'(예: DOBONG, HOURLY) 절대 사용 금지. 반드시 위 '라벨'(예: 도봉, 시급)만 출력.
                2) 라벨이 불명확하거나 해당 없음 → 해당 필드는 비우고, 원문은 keywords 배열에 보존. 목록에서 항목 구분자는 "/" 입니다.
                """.formatted(
                jobFieldLabels,
                workPlaceLabels,
                workTypeLabels,
                workDaysLabels,
                paymentTypeLabels,
                employmentTypeLabels,
                educationReqLabels,
                applyMethodLabels
        );
    }

    // 한국어 지명 → WorkPlace enum (업로드된 enum의 @JsonValue label 사용)
    public static Set<WorkPlace> resolveWorkPlace(Collection<String> tokens) {
        Set<WorkPlace> out = new LinkedHashSet<>();
        for (String tk : tokens) {
            String s = tk.trim();
            // 그대로 / '구' 제거 / '시' 제거 세 가지 모두 시도
            for (String cand : List.of(s, s.replaceAll("구$", ""), s.replaceAll("시$", ""))) {
                WorkPlace wp = WorkPlace.fromLabelNullable(cand); // enum에 있는 helper 활용
                if (wp != null) out.add(wp);
            }
            // enum 코드 직접 입력도 허용 (예: DOBONG)
            try {
                out.add(WorkPlace.valueOf(s));
            } catch (Exception ignore) {
            }
        }
        return out;
    }
}
