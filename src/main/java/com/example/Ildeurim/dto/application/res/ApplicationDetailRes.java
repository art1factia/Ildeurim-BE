package com.example.Ildeurim.dto.application.res;

import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.domain.quickAnswer.AnswerItem;
import com.example.Ildeurim.domain.quickAnswer.QuestionItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDetailRes{
        private Long applicationId;
        private Long jobPostId;
        private Long workerId;

        private ApplicationStatus status;
        private LocalDateTime submissionTime;
        private String applyMethod; // 지원 방법 (예: "간편지원", "전화")

        // 지원자 기본 정보
        private String workerName;
        private LocalDate workerBirthDate;
        private String workerGender;
        private String workerPhoneNumber;
        private String workerAddress;

        // 추가 질문 답변
        private List<AnswerItem> answers;
        private List<QuestionItem> question; //질문 리스트

        public static ApplicationDetailRes of(Application application) {
        Worker worker = application.getWorker();
        JobPost jobPost = application.getJobPost();

        return ApplicationDetailRes.builder()
                .applicationId(application.getId())
            .jobPostId(application.getJobPost().getId())
            .workerId(worker.getId())
            .status(application.getApplicationStatus())
            .submissionTime(application.getSubmissionTime())
            .workerName(worker.getName())
            .workerBirthDate(worker.getBirthday())
            .workerGender(worker.getGender().getLabel())  // enum → 문자열
            .workerPhoneNumber(worker.getPhoneNumber())
            .workerAddress(worker.getResidence())
            .answers(application.getAnswers().items())
                .question(jobPost.getQuestionList().items())
                .applyMethod(application.getApplyMethod().getLabel())
            .build();
}
}

