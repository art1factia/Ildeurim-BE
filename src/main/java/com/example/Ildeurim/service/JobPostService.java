package com.example.Ildeurim.service;

import com.example.Ildeurim.auth.AuthContext;
import com.example.Ildeurim.command.JobPostUpdateCmd;
import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.commons.enums.jobpost.JobPostStatus;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.Employer;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.dto.jobpost.*;
import com.example.Ildeurim.exception.jobPost.JobPostNotFoundException;
import com.example.Ildeurim.exception.jobPost.JobPostPermissionException;
import com.example.Ildeurim.mapper.*;
import com.example.Ildeurim.repository.ApplicationRepository;
import com.example.Ildeurim.repository.EmployerRepository;
import com.example.Ildeurim.repository.JobPostRepository;
import com.example.Ildeurim.repository.WorkerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class JobPostService {
    private final JobPostRepository jobPostRepository;
    private final EmployerRepository employerRepository;
    private final JobPostUpdateCmdMapper jobPostUpdateCmdMapper;
    private final JobFieldMapper jobFieldMapper;
    private final ApplyMethodMapper applyMethodMapper;
    private final WorkDaysMapper workDaysMapper;
    private final WorkPlaceMapper workPlaceMapper;
    private final DateMapper dateMapper;
    private final ApplicationRepository applicationRepository;
    private final WorkerRepository workerRepository;

    //공지글 전체 조회
    @Transactional(readOnly = true)
    public List<SimpleJobPostRes> getAllJobPost() {
        List<JobPost> JobPostList = jobPostRepository.findAll();
        List<SimpleJobPostRes> SimplePostResponseList = JobPostList.stream()
                .map(jobPost -> SimpleJobPostRes.of(jobPost))
                .toList();
        return SimplePostResponseList;
    }

    //공지글 단건 조회
    @Transactional(readOnly = true)
    public JobPostDetailRes getJobPost(long id) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new JobPostNotFoundException(id, "해당 ID의 게시글을 찾을 수 없습니다."));
        return JobPostDetailRes.of(jobPost);
    }

    @Transactional
    public JobPostRes create(JobPostCreateReq req) {
        Long id = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new AccessDeniedException("고용주가 아닙니다."));

        JobPost jobPost = req.toEntity();
        jobPost.setEmployer(employer);
        jobPost.setSaveQuestionList(true);
        jobPost = jobPostRepository.save(jobPost);
        return JobPostRes.from(jobPost);
    }

    @Transactional(readOnly = true)
    public List<SimpleJobPostRes> getJobPostList() {
        Long id = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));

        // 선호 지역 세트(없으면 빈 세트)
        Set<WorkPlace> preferred = workerRepository.findById(id)
                .map(Worker::getBLG)      // Set<WorkPlace>
                .orElse(Collections.emptySet());

        // 전체 공고 조회
        List<JobPost> all = jobPostRepository.findAll();

        // 선호 지역이 없으면 정렬 없이 매핑 후 반환
        if (preferred == null || preferred.isEmpty()) {
            return all.stream().map(SimpleJobPostRes::of).toList();
        }

        // 선호 지역이 있으면: 선호 지역(0) → 비선호(1) → id DESC
        List<JobPost> sorted = all.stream()
                .sorted(
                        Comparator
                                .<JobPost>comparingInt(jp -> preferred.contains(jp.getWorkPlace()) ? 0 : 1)
                                .thenComparing(JobPost::getId, Comparator.reverseOrder())
                )
                .toList();

        return sorted.stream().map(SimpleJobPostRes::of).toList();
    }


    @Transactional(readOnly = true)
    public List<JobPostRes> getMeJobPosts() {
        Long id = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new AccessDeniedException("고용주가 아닙니다."));

        List<JobPost> jobPostList = jobPostRepository.findByEmployer_Id(employer.getId());
        return jobPostList.stream()
                .map(jobPost -> JobPostRes.from(jobPost))
                .toList();
    }

    @Transactional
    public JobPostRes update(Long id, JobPostUpdateReq req) {
        Long userId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        Employer employer = employerRepository.findById(userId)
                .orElseThrow(() -> new AccessDeniedException("고용주가 아닙니다."));
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new JobPostNotFoundException(id, "해당 공고를 찾을 수 없습니다."));
        boolean isMine = jobPost.getEmployer().getId().equals(employer.getId());
        if (!isMine) throw new JobPostPermissionException(id, "공고를 수정할 권한이 없습니다.");
        //TODO: Cmd, Mapper 만들고 update와 연결
        JobPostUpdateCmd cmd = jobPostUpdateCmdMapper.toCmd(req, jobFieldMapper, applyMethodMapper, workDaysMapper, workPlaceMapper, dateMapper);
        jobPost.update(cmd);
        jobPost = jobPostRepository.save(jobPost);
        return JobPostRes.from(jobPost);

    }

    @Transactional
    public void end(Long id) {
        Long userId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        Employer employer = employerRepository.findById(userId)
                .orElseThrow(() -> new AccessDeniedException("고용주가 아닙니다."));
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new JobPostNotFoundException(id, "해당 공고를 찾을 수 없습니다."));
        List<Application> applications = applicationRepository.findByJobPost_IdAndApplicationStatusIsNotAndApplicationStatusIsNot(id, ApplicationStatus.HIRED, ApplicationStatus.ACCEPTED);
        applications.forEach(application -> {
            application.setApplicationStatus(ApplicationStatus.REJECTED);
        });
        boolean isMine = jobPost.getEmployer().getId().equals(employer.getId());
        if (!isMine) throw new JobPostPermissionException(id, "모집 공고를 수정할 권한이 없습니다.");
        jobPost.setStatus(JobPostStatus.CLOSE);
        jobPost = jobPostRepository.save(jobPost);
    }

    @Transactional
    public JobPostRes updateQuestionList(long id, JobPostQuestionListUpdateReq req) {
        Long userId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        Employer employer = employerRepository.findById(userId)
                .orElseThrow(() -> new AccessDeniedException("고용주가 아닙니다."));
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new JobPostNotFoundException(id, "해당 공고를 찾을 수 없습니다."));
        boolean isMine = jobPost.getEmployer().getId().equals(employer.getId());
        if (!isMine) throw new JobPostPermissionException(id, "공고 질문을 수정할 권한이 없습니다.");

        jobPost.setQuestionList(JobPostQuestionListUpdateReq.toQuestionList(req));
        jobPost = jobPostRepository.save(jobPost);
        if (req.saveQuestionList()) {
            employer.setDefaultQuestionList(jobPost.getQuestionList());
            employerRepository.save(employer);
        }
        return JobPostRes.from(jobPost);
    }


}
