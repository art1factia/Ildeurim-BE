package com.example.Ildeurim.service;

import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.domain.quickAnswer.AnswerList;
import com.example.Ildeurim.dto.application.req.ApplicationAnswerUpdateReq;
import com.example.Ildeurim.dto.application.req.ApplicationCreateReq;
import com.example.Ildeurim.dto.application.req.ApplicationModifyReq;
import com.example.Ildeurim.dto.application.req.ApplicationStatusUpdateReq;
import com.example.Ildeurim.dto.application.res.ApplicationRes;
import com.example.Ildeurim.repository.ApplicationRepository;
import com.example.Ildeurim.repository.JobPostRepository;
import com.example.Ildeurim.repository.WorkerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final JobPostRepository jobPostRepository;
    private final WorkerRepository workerRepository;

    /*지원서 초안 생성 -> 보안 관련 코드 나중에 추가하기*/
    @Transactional
    public Long addApplication(ApplicationCreateReq req) {
        Worker worker = workerRepository.findById(req.getWorkerId())
                .orElseThrow(()->new IllegalArgumentException("확인할 수 없는 worker ID입니다."));
        JobPost jobPost =jobPostRepository.findById(req.getJobPostId())
                .orElseThrow(()->new IllegalArgumentException("확인할 수 없는 jobpost id입니다."));

        Application newapplication = req.toEntity(jobPost, worker);

        applicationRepository.save(newapplication);
        return newapplication.getId();
    }

    /*임시 저장*/
    @Transactional
    public void addAnswerToApplication(ApplicationAnswerUpdateReq req) {
        Application application = applicationRepository.findById(req.applicationId())
                .orElseThrow(() -> new IllegalArgumentException("Application을 찾을 수 없습니댜"));

        AnswerList updatedAnswers = application.getAnswers().addItem(req.answer());
        application.setAnswers(updatedAnswers);
    }

    /*최종 저장*/
    @Transactional
    public ApplicationRes submitApplication(ApplicationStatusUpdateReq req) {
        Application application = applicationRepository.findById(req.applicationId())
                .orElseThrow(() -> new IllegalArgumentException("Application not found."));

        application.submit();

        return ApplicationRes.of(application);
    }

    /*지원서 수정*/
    @Transactional
    public void modifyApplication(ApplicationModifyReq req) {
        Application application = applicationRepository.findById(req.applicationId())
                .orElseThrow(() -> new IllegalArgumentException("Application을 찾을 수 없습니다."));

        if (application.getApplicationStatus() == ApplicationStatus.PENDING) {
            throw new IllegalStateException("최종 상태의 지원서는 수정할 수 없습니다.");
        }

        application.setAnswers(req.answers()); // 새로운 답변 목록으로 교체
        application.setIsCareerIncluding(req.isCareerIncluding());

    }
}
