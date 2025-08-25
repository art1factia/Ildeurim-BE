package com.example.Ildeurim.service;

import com.example.Ildeurim.auth.AuthContext;
import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.Job;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.dto.job.JobContractReq;
import com.example.Ildeurim.dto.job.JobCreateReq;
import com.example.Ildeurim.dto.job.JobRes;
import com.example.Ildeurim.dto.job.JobUpdateReq;
import com.example.Ildeurim.repository.ApplicationRepository;
import com.example.Ildeurim.exception.job.JobNotFoundException;
import com.example.Ildeurim.exception.job.JobPermissionException;
import com.example.Ildeurim.exception.jobPost.JobPostNotFoundException;
import com.example.Ildeurim.repository.JobRepository;
import com.example.Ildeurim.repository.WorkerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final WorkerRepository workerRepository;
    private final ObjectStorageService storage;
    private final ApplicationRepository applicationRepository;

    // 근로 생성 (isWorking 기본 true)
    @Transactional
    public JobRes create(JobCreateReq req) {
        Long loginUserId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));

        // 본인 Worker를 인증 컨텍스트로 식별 (ERD 필드만 사용: 요청 바디에 workerId 불필요)
        Worker worker = workerRepository.findById(loginUserId)
                .orElseThrow(() -> new EntityNotFoundException("worker not found"));
        Application application = applicationRepository.findById(req.applicationId())
                .orElseThrow(() -> new EntityNotFoundException("application not found"));
        if (!(application.getApplicationStatus().equals(ApplicationStatus.HIRED))) {
            throw new AccessDeniedException("application is not accepted");
        }
        Job job = Job.builder()
                .worker(worker)
                .application(application)
                .isWorking(true)                     // 기본값
                .jobTitle(req.jobTitle())
                .workPlace(WorkPlace.fromLabel(req.workPlace()))
                .build();

        job = jobRepository.save(job);
        return toRes(job);
    }

    // 특정 worker의 진행중 Job 리스트 반환 (isWorking = true)
    @Transactional(readOnly = true)
    public List<JobRes> getList(Long workerId) {
        List<Job> jobs = jobRepository.findByWorkerIdAndIsWorking(workerId, true);
        List<JobRes> result = new ArrayList<>();
        for (Job job : jobs) {
            result.add(toRes(job));
        }

        return result;
    }

    // Job 단건 조회
    @Transactional(readOnly = true)
    public JobRes get(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobPostNotFoundException(id,"해당 근로를 찾을 수 없습니다."));
        return toRes(job);
    }

    // Job 수정 (부분 수정)
    @Transactional
    public JobRes update(Long id, JobUpdateReq req) {
        Long loginUserId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobPostNotFoundException(id,"해당 근로를 찾을 수 없습니다."));

        if (!job.getWorker().getId().equals(loginUserId)) {
            throw new JobPermissionException(id,"근로를 수정할 권한이 없습니다.");
        }

        if (req.jobTitle() != null) job.setJobTitle(req.jobTitle());
        if (req.workPlace() != null) job.setWorkPlace(WorkPlace.fromLabelNullable(req.workPlace()));
        if (req.contractUrl() != null) job.setContractUrl(req.contractUrl());
        if (req.contractCore() != null) job.setContractCore(req.contractCore());

        job = jobRepository.save(job);
        return toRes(job);
    }

    // Job 종료 처리 (isWorking = false)
    @Transactional
    public void end(Long id) {
        Long loginUserId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id,"해당 근로를 찾을 수 없습니다."));

        if (!job.getWorker().getId().equals(loginUserId)) {
            throw new JobPermissionException(id,"근로를 종료할 권한이 없습니다.");
        }

        job.setIsWorking(false);
        jobRepository.save(job);
    }

    // 계약서 추가/수정
    @Transactional
    public JobRes upsertContract(Long id, MultipartFile file) {
        Long loginUserId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobPostNotFoundException(id,"근로를 찾을 수 없습니다."));

        if (!job.getWorker().getId().equals(loginUserId)) {
            throw new JobPermissionException(id,"계약서를 수정할 권한이 없습니다.");
        }
        String newContractUrl = storage.uploadContract(job.getId(), file, job.getContractUrl());

        job.setContractUrl(newContractUrl);
        //TODO: contract 생성 GPT 연동
//        job.setContractCore(req.contractCore());

        job = jobRepository.save(job);
        return toRes(job);
    }

    // ====== 내부 매퍼: Entity -> DTO ======
    private JobRes toRes(Job j) {
        return new JobRes(
                j.getId(),
                j.getWorker() != null ? j.getWorker().getId() : null,
                j.getIsWorking(),
                j.getJobTitle(),
                j.getWorkPlace(),
                j.getContractUrl(),
                j.getContractCore()
        );
    }
}
