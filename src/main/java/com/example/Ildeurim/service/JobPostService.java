package com.example.Ildeurim.service;

import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.dto.response.PostDetailResponse;
import com.example.Ildeurim.dto.response.SimplePostResponse;
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
    public List<SimplePostResponse> getAllJobPost(){
        List<JobPost> JobPostList =  jobPostRepository.findAll();
        List<SimplePostResponse> SimplePostResponseList = JobPostList.stream().map(jobPost -> SimplePostResponse.of(jobPost)).toList();
        return SimplePostResponseList;
    }

    //공지글 단건 조회
    public PostDetailResponse getJobPost(long id){
        JobPost jobPost= jobPostRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("해당 ID의 게시글을 찾을 수 없습니다. ID: "+id));


    }

}
