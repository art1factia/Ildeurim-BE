package com.example.Ildeurim.service;

import com.example.Ildeurim.auth.AuthContext;
import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import com.example.Ildeurim.commons.enums.jobpost.ApplyMethod;
import com.example.Ildeurim.commons.enums.jobpost.JobPostStatus;
import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.Employer;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.domain.quickAnswer.AnswerList;
import com.example.Ildeurim.domain.quickAnswer.QuestionItem;
import com.example.Ildeurim.dto.application.req.*;
import com.example.Ildeurim.dto.application.res.ApplicationDetailRes;
import com.example.Ildeurim.dto.application.res.ApplicationListRes;
import com.example.Ildeurim.dto.application.res.ApplicationRes;
import com.example.Ildeurim.dto.application.res.SimpleApplicationRes;
import com.example.Ildeurim.jwt.JwtUtil;
import com.example.Ildeurim.repository.ApplicationRepository;
import com.example.Ildeurim.repository.JobPostRepository;
import com.example.Ildeurim.repository.WorkerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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

    /* 지원서 초안 생성 */
    @Transactional
    public Long createApplication(ApplicationCreateReq req) {
        // 1. JWT 토큰에서 현재 사용자 ID를 가져옵니다.
        Long workerId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new AccessDeniedException("User is not a worker"));

        JobPost jobPost = jobPostRepository.findById(req.getJobPostId())
                .orElseThrow(() -> new EntityNotFoundException("Job post not found"));

        // 유효한 공고(OPEN 상태)인지 확인
        if (jobPost.getStatus() != JobPostStatus.OPEN) {
            throw new IllegalStateException("해당 공고는 채용 중이 아닙니다.");
        }

        // 중복 지원서 여부 확인
        boolean exists = applicationRepository.existsByWorkerIdAndJobPostIdAndApplicationStatusIn(
                workerId,
                req.getJobPostId(),
                List.of(ApplicationStatus.DRAFT, ApplicationStatus.NEEDINTERVIEW,ApplicationStatus.PENDING)
        );
        if (exists) {
            throw new IllegalStateException("이미 지원서가 존재합니다.");
        }

        Application newApplication = req.toEntity(jobPost, worker);
        applicationRepository.save(newApplication);
        return newApplication.getId();
    }

    /* 임시 저장, 지원서 수정 */
    @Transactional
    public void addAnswerToApplication(Long applicationId, ApplicationAnswerUpdateReq req) {
        //1. 사용자 조회
        Long workerId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        // 2. 지원서 소유자 확인
        boolean isOwner = application.getWorker().getId().equals(workerId);
        if (!isOwner) throw new AccessDeniedException("No access to update application");

        //draft 일때 만 수정 가능
        if (application.getApplicationStatus() != ApplicationStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT applications can be modified.");
        }

        // 질문 ID 검증
        Long questionId = req.answer().questionId();
        List<Long> validQuestionIds = application.getJobPost()
                .getQuestionList()
                .items()
                .stream()
                .map(QuestionItem::id)
                .toList();

        if (!validQuestionIds.contains(questionId)) {
            throw new IllegalArgumentException(
                    "Invalid questionId %d for jobPost %d".formatted(
                            questionId, application.getJobPost().getId()
                    )
            );
        }

        AnswerList updatedAnswers = application.getAnswers().addItem(req.answer());
        application.setAnswers(updatedAnswers);
        application.setIsCareerIncluding(req.isCareerIncluding());
    }

    /* 최종 저장 */
    @Transactional
    public ApplicationRes submitApplication(Long applicationId) {
        //1. 유저 확인
        Long workerId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        // 2. 지원서 소유자 확인
        boolean isOwner = application.getWorker().getId().equals(workerId);
        if (!isOwner) throw new AccessDeniedException("No access to submit application");

        application.submit();

        return ApplicationRes.of(application);
    }

    /* 지원서 수정 */
//    @Transactional
//    public void modifyApplication(Long applicationId, ApplicationModifyReq req) {
//        //1. 유효 유저 확인
//        Long workerId = AuthContext.userId()
//                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));
//
//        //2. 지원서 확인
//        Application application = applicationRepository.findById(applicationId)
//                .orElseThrow(() -> new EntityNotFoundException("Application not found"));
//
//        // 3. 지원서 소유자 확인
//        boolean isOwner = application.getWorker().getId().equals(workerId);
//        if (!isOwner) throw new AccessDeniedException("No access to modify application");
//
//        //4. draft일 때만 수정 가능
//        if (application.getApplicationStatus() != ApplicationStatus.DRAFT) {
//            throw new IllegalStateException("Only DRAFT applications can be modified.");
//        }
//
//        application.setAnswers(req.answers());
//        application.setIsCareerIncluding(req.isCareerIncluding());
//    }

    /* 지원서 삭제 */
    @Transactional
    public void deleteApplication(Long applicationId) {
        Long workerId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        boolean isOwner = application.getWorker().getId().equals(workerId);
        if (!isOwner) throw new AccessDeniedException("No access to delete application");

        // DRAFT 상태만 삭제 가능
        if (application.getApplicationStatus() != ApplicationStatus.DRAFT) {
            throw new IllegalStateException("제출된 지원서는 삭제할 수 없습니다.");
        }

        applicationRepository.delete(application);
    }


    /*--------------------- 전화 지원 조회 관련 서비스 ---------------------*/
    @Transactional
    public Long createPhoneApplication(PhoneApplicationReq req) {
        Long workerId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new AccessDeniedException("User is not a worker"));

        JobPost jobPost = jobPostRepository.findById(req.getJobPostId())
                .orElseThrow(() -> new EntityNotFoundException("Job post not found"));

        // 중복 지원서 여부 확인
        boolean exists = applicationRepository.existsByWorkerIdAndJobPostIdAndApplicationStatusIn(
                workerId,
                req.getJobPostId(),
                List.of(ApplicationStatus.DRAFT, ApplicationStatus.NEEDINTERVIEW,ApplicationStatus.PENDING)
        );
        if (exists) {
            throw new IllegalStateException("이미 지원서가 존재합니다.");
        }

        Application newApplication = Application.builder()
                .submissionTime(LocalDateTime.now())
                .applicationStatus(ApplicationStatus.NEEDINTERVIEW) // '면접 진행 전' 상태로 바로 설정
                .applyMethod(ApplyMethod.PHONE)
                .isCareerIncluding(true) // 이력서 포함
                .jobPost(jobPost)
                .worker(worker)
                .answers(AnswerList.empty()) // 답변 리스트 없음
                .build();

        applicationRepository.save(newApplication);
        return newApplication.getId();
    }


    /*--------------------- 구직자의 지원 조회 관련 서비스 ---------------------*/
    public List<SimpleApplicationRes> getMyApplications() {
       //1. 사용자 아이디 가져오기
        Long workerId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));

        // 2. 사용자 ID를 바탕으로 지원서 조회
        List<Application> applications = applicationRepository.findByWorkerId(workerId);

        // 3. DTO로 변환하여 반환
        return applications.stream()
                .map(SimpleApplicationRes::of)
                .collect(Collectors.toList());
    }

    /*--------------------- 상세 이력서 확인 서비스---------------------*/
    public ApplicationDetailRes getApplicationDetails(Long applicationId) {
        // 1. 사용자 id
        Long authenticatedUserId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));

        // 2. 지원서 조회
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원서를 찾을 수 없습니다."));

        // 3. 고용주이거나, 지원서의 소유자인지 확인
        Long employerId = application.getJobPost().getEmployer().getId();
        Long workerId = application.getWorker().getId();

        boolean hasAccess = authenticatedUserId.equals(employerId) || authenticatedUserId.equals(workerId);

        if (!hasAccess) {
            throw new AccessDeniedException("이 지원서를 조회할 권한이 없습니다.");
        }

        // 4. 지원서 정보 반환
        return ApplicationDetailRes.of(application);
    }


    /*--------------------- 고용주 목록 조회 ---------------------*/
//    public List<ApplicationListRes> getApplicantsList(Long jobPostId) {
//        // 1. 현재 로그인한 고용주 ID를 가져옵니다.
//        //Long employerId = JwtUtil.getEmployerId();
//
//        // 2. 해당 모집 공고를 조회하고, 현재 고용주의 소유인지 확인하여 보안을 강화합니다.
//        JobPost jobPost = jobPostRepository.findById(jobPostId)
//                .orElseThrow(() -> new IllegalArgumentException("모집 공고를 찾을 수 없습니다."));
//
//        //if (!jobPost.getEmployer().getId().equals(employerId)) {
//        //    throw new SecurityException("자원 접근 권한이 없습니다.");
//        //}
//
//        List<Application> applications = applicationRepository.findByJobPost(jobPost);
//
//        return applications.stream()
//                .map(ApplicationListRes::of)
//                .collect(Collectors.toList());
//    }

    /*--------------------- 지원 상태 변화---------------------*/

    /*지원자 상태 변환*/
    @Transactional
    public void updateApplicationStatus(Long applicationId, String newStatus) {
        // 1. 고용주 ID 가져오기
        Long employerId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원서를 찾을 수 없습니다."));

        // 2. 현재 고용주 ID와 모집공고의 고용주 ID 비교
        boolean isOwner = application.getJobPost().getEmployer().getId().equals(employerId);
        if (!isOwner) {
            throw new AccessDeniedException("자원 접근 권한이 없습니다.");
        }

        // 3. 문자열 상태값을 Enum으로 변환하여 업데이트
        ApplicationStatus status = ApplicationStatus.valueOf(newStatus.toUpperCase()); //TODO:여기 다시 생각해보기
        application.updateStatus(status);
        applicationRepository.save(application);
    }


    /*마감된 채용공고에 대해 불합격자 상태 처리*/
//    @Transactional
//    public void rejectNonHiredApplicants(Long jobPostId) {
//        // 1. 모집 공고의 소유주 확인 (보안 로직)
//        // Long employerId = SecurityUtils.getCurrentEmployerId();
//        // JobPost jobPost = jobPostRepository.findById(jobPostId)
//        //         .orElseThrow(() -> new IllegalArgumentException("모집 공고를 찾을 수 없습니다."));
//        // if (!jobPost.getEmployer().getId().equals(employerId)) {
//        //     throw new SecurityException("자원 접근 권한이 없습니다.");
//        // }
//
//        JobPost jobPost = jobPostRepository.findById(jobPostId)
//                .orElseThrow(() -> new IllegalArgumentException("모집 공고를 찾을 수 없습니다."));
//
//        // 1. JobPost의 상태가 CLOSED인지 확인
//        if (jobPost.getStatus() != JobPostStatus.CLOSE) {
//            throw new IllegalStateException("모집 공고가 마감되지 않았습니다.");
//        }
//
//        // 2. 최종 합격자를 제외한 지원자들의 상태를 REJECTED로 변경
//        List<Application> applications = applicationRepository.findByJobPost(jobPost);
//        for (Application app : applications) {
//            if (app.getApplicationStatus() != ApplicationStatus.ACCEPTED
//                    ||app.getApplicationStatus() != ApplicationStatus.HIRED) {
//                app.updateStatus(ApplicationStatus.REJECTED);
//            }
//        }
//        applicationRepository.saveAll(applications);
//    }

}
