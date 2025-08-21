package com.example.Ildeurim.dto.jobpost;

import com.example.Ildeurim.commons.enums.jobpost.ApplyMethod;
import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.domain.JobPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class SimpleJobPostRes {
    private Long jobPostId;                       // 공고 ID (상세페이지 들어갈 때 필요)
    private String companyName;                   // 구인 업체명
    private String title;                         // 구인 공고명
    private WorkPlace workPlace;                  // 지역
    private String location;                      // 위치 (세부 주소)
    private Set<ApplyMethod> applyMethods;       // 지원 방법 (간편지원, 전화지원 등)
    private LocalDateTime expiryDate;             // 채용 마감 기한
    private Set<JobField> jobFields;             // 구직 분야

    public static SimpleJobPostRes of(JobPost jobPost)
    { return SimpleJobPostRes.builder()
            .jobPostId(jobPost.getId())
            .title(jobPost.getTitle())
            .companyName(jobPost.getEmployer().getCompanyName())
            .applyMethods(jobPost.getApplyMethods())
            .location(jobPost.getLocation())
            .jobFields(jobPost.getEmployer().getJobFields())
            .expiryDate(jobPost.getExpiryDate())
            .build();
            }
}
