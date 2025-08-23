package com.example.Ildeurim.service;

import com.example.Ildeurim.auth.AuthContext;
import com.example.Ildeurim.command.JobPostUpdateCmd;
import com.example.Ildeurim.commons.enums.jobpost.JobPostStatus;
import com.example.Ildeurim.domain.Employer;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.dto.jobpost.*;
import com.example.Ildeurim.mapper.*;
import com.example.Ildeurim.repository.EmployerRepository;
import com.example.Ildeurim.repository.JobPostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 게시글을 찾을 수 없습니다. ID: " + id));
        return JobPostDetailRes.of(jobPost);
    }

    @Transactional
    public JobPostRes create(JobPostCreateReq req) {
        Long id = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new AccessDeniedException("user is not employer"));

        JobPost jobPost = req.toEntity();
        jobPost.setEmployer(employer);
        jobPost = jobPostRepository.save(jobPost);
        return JobPostRes.from(jobPost);
    }

    @Transactional(readOnly = true)
    public List<SimpleJobPostRes> getJobPostList(JobPostFilter req){
        List<JobPost> jobPostList = jobPostRepository.findAll(); //TODO: findAll 대신 필터 기반으로 변경
        List<SimpleJobPostRes> jobPostResList = jobPostList.stream()
                .map(article -> SimpleJobPostRes.of(article))
                .toList();
        return jobPostResList;
    }

    @Transactional
    public JobPostRes update(Long id, JobPostUpdateReq req) {
        Long userId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));
        Employer employer = employerRepository.findById(userId)
                .orElseThrow(() -> new AccessDeniedException("user is not employer"));
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("jobPost not found"));
        boolean isMine = jobPost.getEmployer().getId().equals(employer.getId());
        if (!isMine) throw new AccessDeniedException("no access to update job post");
        //TODO: Cmd, Mapper 만들고 update와 연결
        JobPostUpdateCmd cmd = jobPostUpdateCmdMapper.toCmd(req, jobFieldMapper, applyMethodMapper, workDaysMapper, workPlaceMapper, dateMapper);
        jobPost.update(cmd);
        jobPost = jobPostRepository.save(jobPost);
        return JobPostRes.from(jobPost);

    }

    @Transactional
    public void end(Long id) {
        Long userId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));
        Employer employer = employerRepository.findById(userId)
                .orElseThrow(() -> new AccessDeniedException("user is not employer"));
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("jobPost not found"));
        boolean isMine = jobPost.getEmployer().getId().equals(employer.getId());
        if (!isMine) throw new AccessDeniedException("no access to update job post");
        jobPost.setStatus(JobPostStatus.CLOSE);
        jobPost = jobPostRepository.save(jobPost);
    }


}
