package com.example.Ildeurim.commons.converter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public final class DateParsers {
    private DateParsers() {}

    // 표준 ISO 포맷
    private static final DateTimeFormatter ISO_DATE   = DateTimeFormatter.ISO_LOCAL_DATE;        // yyyy-MM-dd
    private static final DateTimeFormatter ISO_LDT    = DateTimeFormatter.ISO_LOCAL_DATE_TIME;   // yyyy-MM-dd'T'HH:mm:ss[.SSS]
    private static final DateTimeFormatter ISO_ODT    = DateTimeFormatter.ISO_OFFSET_DATE_TIME;  // yyyy-MM-dd'T'HH:mm:ssXXX

    // 자주 쓰는 커스텀(분까지)
    private static final DateTimeFormatter YMdhm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static LocalDate parseLocalDate(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDate.parse(s.trim(), ISO_DATE);
    }

    /** 여러 패턴 허용 (예: "2025-08-22T12:00", "2025-08-22 12:00", "2025-08-22T12:00:30") */
    public static LocalDateTime parseLocalDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim();
        List<DateTimeFormatter> candidates = List.of(ISO_LDT, YMdhm);
        for (DateTimeFormatter f : candidates) {
            try { return LocalDateTime.parse(t, f); } catch (DateTimeParseException ignore) {}
        }
        // 만약 오프셋(+09:00/Z)이 붙어오면 ODT로 파싱 후 '벽시각'만 사용
        try { return OffsetDateTime.parse(t, ISO_ODT).toLocalDateTime(); } catch (DateTimeParseException ignore) {}
        throw new IllegalArgumentException("Invalid LocalDateTime: " + s);
    }

    /** 오프셋/타임존이 들어온 문자열은 그대로 보존하고 싶을 때 */
    public static OffsetDateTime parseOffsetDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        return OffsetDateTime.parse(s.trim(), ISO_ODT);
    }

    /** 문자열을 서울 시간대로 해석해 UTC로 Instant 얻기 (저장/연산용) */
    public static Instant parseSeoulToInstant(String s) {
        LocalDateTime ldt = parseLocalDateTime(s);
        return ldt.atZone(ZoneId.of("Asia/Seoul")).toInstant();
    }
    // 문자열 → LocalTime
    /** 허용 예: "09:30", "09:30:15", "09:30:15.123", "0930", "09:30+09:00" */
    public static LocalTime parseLocalTime(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim();

        // 1) ISO_LOCAL_TIME: HH:mm[:ss[.SSS]]
        try { return LocalTime.parse(t, DateTimeFormatter.ISO_LOCAL_TIME); }
        catch (DateTimeParseException ignore) {}

        // 2) HHmm (분까지만 붙여 쓴 형태)
        try { return LocalTime.parse(t, DateTimeFormatter.ofPattern("HHmm")); }
        catch (DateTimeParseException ignore) {}

        // 3) 오프셋이 포함된 경우(예: 09:30+09:00) → 로컬타임만 사용
        try { return OffsetTime.parse(t, DateTimeFormatter.ISO_OFFSET_TIME).toLocalTime(); }
        catch (DateTimeParseException ignore) {}

        throw new IllegalArgumentException("Invalid LocalTime: " + s);
    }
}
