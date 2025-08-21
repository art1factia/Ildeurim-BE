package com.example.Ildeurim.dto.application.res;

import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.domain.Application;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SimpleApplicationRes {
    private Long applicationId;
    private Long jobPostId;
    private String companyName;
    private String title;
    private String location;
    private JobField jobField;
    private String applicationStatus;

    public static SimpleApplicationRes of(Application application) {
        return SimpleApplicationRes.builder()
                .applicationId(application.getId())
                .jobPostId(application.getJobPost().getId())
                .companyName(application.getJobPost().getEmployer().getCompanyName())
                .title(application.getJobPost().getTitle())
                .location(application.getJobPost().getLocation())
                .jobField(application.getJobPost().getJobField())
                .applicationStatus(application.getApplicationStatus().getLabel())
                .build();
    }
}
