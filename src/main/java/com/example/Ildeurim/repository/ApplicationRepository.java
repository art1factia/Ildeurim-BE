package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByWorkerId(Long workerId);

    List<Application> findByJobPost(JobPost jobPost);
    // 파생 쿼리: Application.worker.id 로 카운트
    long countByWorkerId(Long workerId);
}
