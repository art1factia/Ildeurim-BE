package com.example.Ildeurim.command;

import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.commons.enums.worker.Gender;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public record WorkerUpdateCmd(
        Optional<String> name,
        Optional<String> phoneNumber,
        Optional<LocalDate> birthday,
        Optional<Gender> gender,
        Optional<String> residence,
        Optional<String> RLG,
        Optional<Set<WorkPlace>> BLG,
        Optional<Set<JobField>> jobInterest
) {
    // ---- helpers ----
    private static <T> Optional<T> nn(Optional<T> o) { return (o == null) ? Optional.empty() : o; }
    private static <T> Optional<T> opt(T v)          { return Optional.ofNullable(v); }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static <T> Optional<Set<T>> cleanSetOpt(Optional<Set<T>> os) {
        if (os == null || os.isEmpty()) return Optional.empty();
        Set<T> src = os.orElseGet(Set::of);
        // mutable 로 수집
        LinkedHashSet<T> cleaned = src.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return cleaned.isEmpty() ? Optional.empty() : Optional.of(cleaned); // <-- 불변 래핑 제거
    }

    // ---- defensive canonical constructor ----
    public WorkerUpdateCmd {
        name        = nn(name).flatMap(v -> opt(trimToNull(v)));
        phoneNumber = nn(phoneNumber).flatMap(v -> opt(trimToNull(v)));
        birthday    = nn(birthday);
        gender      = nn(gender);
        residence   = nn(residence).flatMap(v -> opt(trimToNull(v)));
        RLG         = nn(RLG).flatMap(v -> opt(trimToNull(v)));
        BLG         = cleanSetOpt(BLG);
        jobInterest = cleanSetOpt(jobInterest);
    }
}
