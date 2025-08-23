package com.example.Ildeurim.dto.jobpost;

import com.example.Ildeurim.commons.enums.QuestionType;
import com.example.Ildeurim.domain.quickAnswer.QuestionItem;
import com.example.Ildeurim.domain.quickAnswer.QuestionList;

import java.util.List;

import java.util.*;

/**
 * req → QuestionList 변환 + 유효성/정규화
 * - items null 금지
 * - 각 item: null 금지, text 공백 금지, type null 금지
 * - id가 있는 항목은 중복 금지
 * - options: null이면 빈 리스트로, null/공백 요소 제거 + 중복 제거(순서 유지)
 */
public record JobPostQuestionListUpdateReq(
        List<QuestionItem> items
) {
    public static QuestionList toQuestionList(JobPostQuestionListUpdateReq req) {
        if (req == null) throw new IllegalArgumentException("req must not be null");
        final List<QuestionItem> src = Objects.requireNonNull(req.items(), "items must not be null");

        final List<QuestionItem> sanitized = new ArrayList<>(src.size());
        final Set<Long> seenIds = new HashSet<>();

        for (int i = 0; i < src.size(); i++) {
            final QuestionItem it = Objects.requireNonNull(src.get(i), "items[" + i + "] must not be null");

            final String text = Optional.ofNullable(it.text())
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .orElseThrow(() -> new IllegalArgumentException("items[].text must not be blank"));

            final QuestionType type = Objects.requireNonNull(it.type(), "items[" + i + "].type must not be null");

            final Long id = it.id();
            if (id != null && !seenIds.add(id)) {
                throw new IllegalArgumentException("duplicate id detected: " + id);
            }

            // options 정규화: null → 빈리스트, null/공백 제거, 중복 제거(순서 유지)
            List<String> normalizedOptions = List.of();
            if (it.options() != null) {
                final LinkedHashSet<String> dedup = new LinkedHashSet<>();
                for (int j = 0; j < it.options().size(); j++) {
                    final String opt = Optional.ofNullable(it.options().get(j))
                            .map(String::trim)
                            .orElse("");
                    if (!opt.isEmpty()) dedup.add(opt);
                }
                normalizedOptions = List.copyOf(dedup);
            }

            sanitized.add(new QuestionItem(id, text, type, normalizedOptions));
        }

        // 최종 불변 리스트로 래핑
        return new QuestionList(List.copyOf(sanitized));
    }
}

