package com.example.Ildeurim.service.jobpost;

import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.dto.jobpost.JobPostDetailRes;
import com.example.Ildeurim.dto.jobpost.SimpleJobPostRes;
import com.example.Ildeurim.repository.JobPostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobPostService {
    private final JobPostRepository jobPostRepository;

    //공지글 전체 조회
    public List<SimpleJobPostRes> getAllJobPost() {
        List<JobPost> JobPostList = jobPostRepository.findAll();
        List<SimpleJobPostRes> SimplePostResponseList = JobPostList.stream()
                .map(jobPost -> SimpleJobPostRes.of(jobPost))
                .toList();
        return SimplePostResponseList;
    }

}
