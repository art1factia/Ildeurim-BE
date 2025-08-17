package com.example.Ildeurim.dto.response;

import com.example.Ildeurim.commons.enums.JobPostStatus;
import com.example.Ildeurim.domain.JobPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SimplePostResponse {
    private Long jobPostId;           // 공고 ID
    private String title;             // 공고명
    private String companyName;      // 구인 업체명
    private String location;          // 근무지/지역
    private JobPostStatus status;

    public static SimplePostResponse of(JobPost jobPost)
    { return SimplePostResponse.builder()
            .jobPostId(jobPost.getId())
            .title(jobPost.getTitle())
            .companyName(jobPost.getEmployer().getCompanyName())
            .location(jobPost.getLocation())
            .status(jobPost.getStatus())
            .build();
            }
}
