package com.example.Ildeurim.controller;

// JobPostSearchController.java
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.dto.jobpost.JobPostFilter;
import com.example.Ildeurim.gpt.EnumCatalog;
import com.example.Ildeurim.gpt.FilterNormalizer;
import com.example.Ildeurim.gpt.FilterPrompts;
import com.example.Ildeurim.gpt.JobPostSpecs;
import com.example.Ildeurim.repository.JobPostRepository;
import com.example.Ildeurim.service.LlmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobposts")
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
    public List<JobPost> search(@RequestBody NLQuery body) {
        // 1) 필터 생성
        JobPostFilter filter = buildFilter(body);
        // 2) 동적 스펙 조립 + 정렬(예: 최신 공고 우선)
        var spec = specs.fromFilter(filter);
        return repo.findAll(spec, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "startDate"));
    }
}
