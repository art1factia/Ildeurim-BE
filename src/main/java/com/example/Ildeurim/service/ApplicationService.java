package com.example.Ildeurim.service;

import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import com.example.Ildeurim.commons.enums.jobpost.JobPostStatus;
import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.domain.quickAnswer.AnswerList;
import com.example.Ildeurim.dto.application.req.ApplicationAnswerUpdateReq;
import com.example.Ildeurim.dto.application.req.ApplicationCreateReq;
import com.example.Ildeurim.dto.application.req.ApplicationModifyReq;
import com.example.Ildeurim.dto.application.req.ApplicationStatusUpdateReq;
import com.example.Ildeurim.dto.application.res.ApplicationListRes;
import com.example.Ildeurim.dto.application.res.ApplicationRes;
import com.example.Ildeurim.dto.application.res.SimpleApplicationRes;
import com.example.Ildeurim.jwt.JwtUtil;
import com.example.Ildeurim.repository.ApplicationRepository;
import com.example.Ildeurim.repository.JobPostRepository;
import com.example.Ildeurim.repository.WorkerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final JobPostRepository jobPostRepository;
    private final WorkerRepository workerRepository;

    /*--------------------- 간편 지원 관련 서비스 ---------------------*/

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

    /*지원서 삭제*/
    @Transactional
    public void deleteApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application이 존재하지 않습니다."));

//        Long workerId = SecurityUtils.getCurrentWorkerId();
//        if (!application.getWorker().getId().equals(workerId)) {
//            throw new SecurityException("자원 접근 권한이 없습니다.");
//        }

        applicationRepository.delete(application);
    }

    /*--------------------- 구직자의 지원 조회 관련 서비스 ---------------------*/
    //보안 코드 추가하기
    public List<SimpleApplicationRes> getMyApplications(Long workerId) {
        //조회
        List<Application> applications = applicationRepository.findByWorkerId(workerId);

        //dto에 맞게 변환
        return applications.stream()
                .map(SimpleApplicationRes::of)
                .collect(Collectors.toList());
    }

    /*--------------------- 고용주 목록 조회 ---------------------*/
    public List<ApplicationListRes> getApplicantsList(Long jobPostId) {
        // 1. 현재 로그인한 고용주 ID를 가져옵니다.
        //Long employerId = JwtUtil.getEmployerId();

        // 2. 해당 모집 공고를 조회하고, 현재 고용주의 소유인지 확인하여 보안을 강화합니다.
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new IllegalArgumentException("모집 공고를 찾을 수 없습니다."));

        //if (!jobPost.getEmployer().getId().equals(employerId)) {
        //    throw new SecurityException("자원 접근 권한이 없습니다.");
        //}

        List<Application> applications = applicationRepository.findByJobPost(jobPost);

        return applications.stream()
                .map(ApplicationListRes::of)
                .collect(Collectors.toList());
    }

    /*--------------------- 지원 상태 변화---------------------*/

    /*지원자 상태 변환*/
    @Transactional
    public void updateApplicationStatus(Long applicationId, String newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원서를 찾을 수 없습니다."));

        // 보안: 현재 고용주가 지원서의 모집공고 소유자인지 확인
        // Long employerId = SecurityUtils.getCurrentEmployerId();
        // if (!application.getJobPost().getEmployer().getId().equals(employerId)) {
        //    throw new SecurityException("자원 접근 권한이 없습니다.");
        // }

        // 문자열 상태값을 enum으로 변환하여 업데이트
        ApplicationStatus status = ApplicationStatus.valueOf(newStatus.toUpperCase());
        application.updateStatus(status);
        applicationRepository.save(application);
    }


    /*마감된 채용공고에 대해 불합격자 상태 처리*/
    @Transactional
    public void rejectNonHiredApplicants(Long jobPostId) {
        // 1. 모집 공고의 소유주 확인 (보안 로직)
        // Long employerId = SecurityUtils.getCurrentEmployerId();
        // JobPost jobPost = jobPostRepository.findById(jobPostId)
        //         .orElseThrow(() -> new IllegalArgumentException("모집 공고를 찾을 수 없습니다."));
        // if (!jobPost.getEmployer().getId().equals(employerId)) {
        //     throw new SecurityException("자원 접근 권한이 없습니다.");
        // }

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new IllegalArgumentException("모집 공고를 찾을 수 없습니다."));

        // 1. JobPost의 상태가 CLOSED인지 확인
        if (jobPost.getStatus() != JobPostStatus.CLOSE) {
            throw new IllegalStateException("모집 공고가 마감되지 않았습니다.");
        }

        // 2. 최종 합격자를 제외한 지원자들의 상태를 REJECTED로 변경
        List<Application> applications = applicationRepository.findByJobPost(jobPost);
        for (Application app : applications) {
            if (app.getApplicationStatus() != ApplicationStatus.ACCEPTED
                    ||app.getApplicationStatus() != ApplicationStatus.HIRED) {
                app.updateStatus(ApplicationStatus.REJECTED);
            }
        }
        applicationRepository.saveAll(applications);
    }

}
