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
import com.example.Ildeurim.exception.application.DuplicateApplicationException;
import com.example.Ildeurim.exception.application.JobPostClosedException;
import com.example.Ildeurim.jwt.JwtUtil;
import com.example.Ildeurim.repository.ApplicationRepository;
import com.example.Ildeurim.repository.EmployerRepository;
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
    private final EmployerRepository employerRepository;
    //

    /*--------------------- 간편 지원 관련 서비스 ---------------------*/

    /* 지원서 초안 생성 */
    @Transactional
    public Long createApplication(ApplicationCreateReq req, Long jobPostId) {
        // 1. JWT 토큰에서 현재 사용자 ID를 가져옵니다.
        Long workerId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new AccessDeniedException("사용자가 근로자가 아닙니다."));

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new EntityNotFoundException("공고를 찾을 수 없습니다."));

        // 유효한 공고(OPEN 상태)인지 확인
        if (jobPost.getStatus() != JobPostStatus.OPEN) {
            throw new JobPostClosedException(jobPostId, "해당 공고는 채용 중이 아닙니다.");
        }

        // 중복 지원서 여부 확인
        boolean exists = applicationRepository.existsByWorkerIdAndJobPostIdAndApplicationStatusIn(
                workerId,
                jobPostId,
                List.of(ApplicationStatus.DRAFT, ApplicationStatus.NEEDINTERVIEW, ApplicationStatus.PENDING)
        );
        if (exists) {
            throw new DuplicateApplicationException(workerId, jobPostId, "이미 지원서가 존재합니다.");
        }

        Application newApplication = req.toEntity(jobPost, worker);
        applicationRepository.save(newApplication);
        return newApplication.getId();
    }

    @Transactional
    public ApplicationRes confirmPhoneApplication(Long id) {
        // 1. JWT 토큰에서 현재 사용자 ID를 가져옵니다.
        Long workerId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new AccessDeniedException("사용자가 근로자가 아닙니다."));
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("지원서가 존재하지 않습니다."));
        if (application.getApplyMethod() != ApplyMethod.PHONE) {
            throw new IllegalStateException("전화 지원으로 접수된 지원서가 아닙니다.");
        }
        if (application.getApplicationStatus() != ApplicationStatus.DRAFT) {
            throw new IllegalStateException("임시 저장된 전화 지원이 아닙니다.");
        }
        application.setApplicationStatus(ApplicationStatus.NEEDINTERVIEW);
        applicationRepository.save(application);
        return ApplicationRes.of(application);

    }

    /* 임시 저장, 지원서 수정 */
    @Transactional
    public void addAnswerToApplication(Long applicationId, ApplicationAnswerUpdateReq req) {
        //1. 사용자 조회
        Long workerId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("지원서를 찾을 수 없습니다."));

        // 2. 지원서 소유자 확인
        boolean isOwner = application.getWorker().getId().equals(workerId);
        if (!isOwner) throw new AccessDeniedException("지원서를 수정할 권한이 없습니다.");

        //draft 일때 만 수정 가능
        if (application.getApplicationStatus() != ApplicationStatus.DRAFT) {
            throw new IllegalStateException("임시 저장 상태의 지원서만 수정할 수 있습니다.");
        }

        JobPost jobPost = application.getJobPost();
        if (jobPost.getStatus() != JobPostStatus.OPEN)
            throw new JobPostClosedException(jobPost.getId(), "모집이 마감된 공고는 더 이상 수정할 수 없습니다.");

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
                    "질문 ID %d가 공고 %d에 유효하지 않습니다.".formatted(
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
                .orElseThrow(() -> new AccessDeniedException("지원서를 제출할 권한이 없습니다."));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("지원서를 찾을 수 없습니다."));

        // 2. 지원서 소유자 확인
        boolean isOwner = application.getWorker().getId().equals(workerId);
        if (!isOwner) throw new AccessDeniedException("지원서를 제출할 권한이 없습니다.");

        // 4. 모집 공고 마감 여부 확인
        JobPost jobPost = application.getJobPost();
        if (jobPost.getStatus() != JobPostStatus.OPEN)
            throw new JobPostClosedException(jobPost.getId(), "모집이 마감된 공고는 제출할 수 없습니다.");

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
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("지원서를 찾을 수 없습니다."));

        boolean isOwner = application.getWorker().getId().equals(workerId);
        if (!isOwner) throw new AccessDeniedException("지원서를 삭제할 권한이 없습니다.");

        // DRAFT 상태만 삭제 가능
        if (application.getApplicationStatus() != ApplicationStatus.DRAFT) {
            throw new IllegalStateException("제출된 지원서는 삭제할 수 없습니다.");
        }

        applicationRepository.delete(application);
    }


    /*--------------------- 전화 지원 조회 관련 서비스 ---------------------*/
    @Transactional
    public ApplicationRes createPhoneApplication(long jobPostId) {
        Long workerId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new AccessDeniedException("사용자가 근로자가 아닙니다."));

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new EntityNotFoundException("공고를 찾을 수 없습니다."));

        // 중복 지원서 여부 확인
        boolean exists = applicationRepository.existsByWorkerIdAndJobPostIdAndApplicationStatusIn(
                workerId,
                jobPostId,
                List.of(ApplicationStatus.DRAFT, ApplicationStatus.DRAFT, ApplicationStatus.PENDING)
        );
        if (exists) {
            throw new DuplicateApplicationException(workerId, jobPostId, "이미 지원서가 존재합니다.");
        }

        Application newApplication = Application.builder()
                .submissionTime(LocalDateTime.now())
                .applicationStatus(ApplicationStatus.DRAFT) // '면접 진행 전' 상태로 바로 설정
                .applyMethod(ApplyMethod.PHONE)
                .isCareerIncluding(true) // 이력서 포함
                .jobPost(jobPost)
                .worker(worker)
                .answers(AnswerList.empty()) // 답변 리스트 없음
                .build();

        newApplication = applicationRepository.save(newApplication);
        return ApplicationRes.of(newApplication);
    }


    /*--------------------- 구직자의 지원 조회 관련 서비스 ---------------------*/
    public List<SimpleApplicationRes> getMyApplications() {
        //1. 사용자 아이디 가져오기
        Long workerId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));

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
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));

        // 2. 지원서 조회
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("지원서를 찾을 수 없습니다."));

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
    public List<ApplicationListRes> getApplicantsList(Long jobPostId) {
        Long userId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        Employer employer = employerRepository.findById(userId)
                .orElseThrow(() -> new AccessDeniedException("사용자가 고용주가 아닙니다."));
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new EntityNotFoundException("공고를 찾을 수 없습니다."));
        boolean isMine = jobPost.getEmployer().getId().equals(employer.getId());
        if (!isMine) throw new AccessDeniedException("모집 공고를 조회할 권한이 없습니다.");

        List<Application> applications = applicationRepository.findByJobPost(jobPost);

        return applications.stream()
                .map(ApplicationListRes::of)
                .collect(Collectors.toList());
    }

    /*--------------------- 지원 상태 변화---------------------*/

    /*지원자 상태 변환*/
    @Transactional
    public void updateApplicationStatus(Long applicationId, ApplicationStatus newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("지원서를 찾을 수 없습니다."));
        Long user = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        if (newStatus == ApplicationStatus.HIRED) {
            if (application.getApplicationStatus() != ApplicationStatus.ACCEPTED) {
                throw new IllegalArgumentException("탈락된 지원서입니다.");
            }
            Worker worker = workerRepository.findById(user)
                    .orElseThrow(() -> {
                        throw new AccessDeniedException("고용주는 채용확정을 할 수 없습니다.");
                    });
            application.setApplicationStatus(newStatus);
        } else {
            Employer employer = employerRepository.findById(user)
                    .orElseThrow(() -> {
                        throw new AccessDeniedException("고용인은 지원서의 상태를 변경할 수 없습니다.");
                    });
            if (!application.getJobPost().getEmployer().getId().equals(employer.getId())) {
                throw new AccessDeniedException("해당 지원서의 상태를 변경할 권한이 없습니다.");
            }
            application.setApplicationStatus(newStatus);
        }
        application = applicationRepository.save(application);
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
