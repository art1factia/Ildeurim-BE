package com.example.Ildeurim.dto.application.res;

import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.domain.quickAnswer.AnswerItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ApplicationRes {
    // 지원서 관련 정보
    private Long applicationId;             // 지원서 ID
    private LocalDateTime submissionTime;   // 지원서 제출 시간
    private String applicationStatus;       // 지원서 상태 (예: "보류", "승인")
    private boolean isCareerIncluding;      // 이력서 포함 여부

    // 지원자 정보
    private String workerName;              // 지원자 이름
    private String workerPhoneNumber;       // 지원자 전화번호

    // 모집 공고 정보
    private String jobPostTitle;            // 모집 공고 제목
    private String companyName;             // 회사명
    private String jobPostLocation;         // 근무 위치

    // 간편 지원 답변 리스트
    private List<AnswerItem> answers;       // 간편 지원 질문에 대한 답변 목록


    public static ApplicationRes of(Application application) {
        Worker worker = application.getWorker();
        JobPost jobPost = application.getJobPost();

        return ApplicationRes.builder()
                .applicationId(application.getId())
                .submissionTime(application.getSubmissionTime())
                .applicationStatus(application.getApplicationStatus().getLabel()) // enum 값을 라벨(String)로 변환
                .isCareerIncluding(application.getIsCareerIncluding())
                .workerName(worker.getName())
                .workerPhoneNumber(worker.getPhoneNumber())
                .jobPostTitle(jobPost.getTitle())
                .companyName(jobPost.getEmployer().getCompanyName())
                .jobPostLocation(jobPost.getLocation())
                .answers(application.getAnswers().items())
                .build();
    }
}