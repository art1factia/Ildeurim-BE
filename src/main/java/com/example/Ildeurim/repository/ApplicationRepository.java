package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // 파생 쿼리: Application.worker.id 로 카운트
    long countByWorkerId(Long workerId);
}
