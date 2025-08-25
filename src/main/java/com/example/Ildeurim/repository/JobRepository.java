package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByWorkerIdAndIsWorking(Long workerId, Boolean isWorking);

    boolean existsByWorker_IdAndApplication_Id(Long workerId, Long applicationId);
}
