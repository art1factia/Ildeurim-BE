package com.example.Ildeurim.dto.application.res;

import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.quickAnswer.AnswerItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.stylesheets.LinkStyle;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ApplicationRes {
    private Long applicationId;            // 지원서 ID
    private String status;                 // 지원서 상태 (예: "SUBMITTED")
    private List<AnswerItem> answers;      // 답변 리스트
    private boolean isCareerIncluding;     // 이력서 포함 여부
    private LocalDateTime submittedAt;     // 제출 시간

    public static ApplicationRes of(Application application) {
        return ApplicationRes.builder()
                .applicationId(application.getId())
                .status(application.getApplicationStatus().getLabel())
                .answers(application.getAnswers().items())
                .isCareerIncluding(application.getIsCareerIncluding())
                .submittedAt(application.getSubmissionTime())
                .build();
    }
}