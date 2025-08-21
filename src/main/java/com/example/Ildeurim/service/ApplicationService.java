package com.example.Ildeurim.service;

import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.domain.quickAnswer.AnswerList;
import com.example.Ildeurim.dto.application.req.ApplicationAnswerUpdateReq;
import com.example.Ildeurim.dto.application.req.ApplicationCreateReq;
import com.example.Ildeurim.dto.application.req.ApplicationStatusUpdateReq;
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
    private JobPostRepository jobPostRepository;
    private WorkerRepository workerRepository;

    /*지원서 초안 생성 -> 보안 관련 코드 나중에 추가하기*/
    @Transactional
    public Long addApplication(ApplicationCreateReq req) {
        Worker worker = workerRepository.findById(req.getWorkerId())
                .orElseThrow(()->new IllegalArgumentException("확일 할 수 없는 worker ID입니다."));
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

    @Transactional
    public void submitApplication(ApplicationStatusUpdateReq req) {
        Application application = applicationRepository.findById(req.applicationId())
                .orElseThrow(() -> new IllegalArgumentException("Application not found."));

        application.setApplicationStatus(ApplicationStatus.PENDING);
        application.setSubmissionTime(LocalDateTime.now());
    }
}
