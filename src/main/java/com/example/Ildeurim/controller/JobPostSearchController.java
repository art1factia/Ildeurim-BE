package com.example.Ildeurim.controller;

// JobPostSearchController.java
import com.example.Ildeurim.commons.enums.jobpost.JobPostStatus;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.dto.jobpost.JobPostFilter;
import com.example.Ildeurim.dto.jobpost.JobPostRes;
import com.example.Ildeurim.gpt.EnumCatalog;
import com.example.Ildeurim.gpt.FilterNormalizer;
import com.example.Ildeurim.gpt.FilterPrompts;
import com.example.Ildeurim.gpt.JobPostSpecs;
import com.example.Ildeurim.repository.JobPostRepository;
import com.example.Ildeurim.service.LlmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobPosts")
@RequiredArgsConstructor
public class JobPostSearchController {
    private final LlmService llm;
    private final JobPostRepository repo;
    private final JobPostSpecs specs;

    public static class NLQuery { public String q; } // {"q":"도봉구에서 월요일에 청소하는 일자리"}

    @PostMapping("/filters")
    public JobPostFilter buildFilter(@RequestBody NLQuery body) {
        String allowed = EnumCatalog.allowedCatalogForPrompt();
        JobPostFilter llmOut = llm.completeJson(FilterPrompts.SYSTEM, FilterPrompts.userPrompt(body.q, allowed), JobPostFilter.class);
        return FilterNormalizer.normalize(llmOut, body.q);
    }

    @PostMapping("/search")
    public List<JobPostRes> search(@RequestBody NLQuery body) {
// 1) 필터 생성 (자유입력 → DTO)
        JobPostFilter filter = buildFilter(body);

        // 2) 선호 workPlace 정리(LinkedHashSet으로 순서 유지)
        var preferred = FilterNormalizer.ensurePreferred(filter);

        // 3) 최소한의 where (예: OPEN만)
        Specification<JobPost> base = specs.statusEq(JobPostStatus.OPEN);
        // 필요하면 isActiveNow() 같은 것도 and로 묶을 수 있음

        // 4) soft-match: workPlace만으로 매칭 판단(마포면 matchRank=0)
        Specification<JobPost> softMatch = specs.workPlaceInForMatch(preferred);

        Specification<JobPost> order = specs.rankByMatchThenWorkPlace(softMatch, preferred);

        // 6) 실행: WHERE=base, ORDER BY=order
        return repo.findAll(base.and(order))
                .stream()
                .map(jobPost -> JobPostRes.from(jobPost))
                .collect(Collectors.toList());
    }
}
