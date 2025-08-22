package com.example.Ildeurim.dto.application.res;

import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.quickAnswer.AnswerItem;
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

        // 지원자 기본 정보
        private String workerName;
        private LocalDate workerBirthDate;
        private String workerGender;
        private String workerPhoneNumber;
        private String workerAddress;

//        // 지원자 이력
//        private List<CareerRes> careers;

        // 추가 질문 답변
        private List<AnswerItem> answers;

        public static ApplicationDetailRes from(Application application) {
    var worker = application.getWorker();

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

//            .careers(worker.getCareers().stream()
//                        .map(CareerRes::from)
//                        .toList())
            .answers(application.getAnswers().items())
            .build();
}
}

