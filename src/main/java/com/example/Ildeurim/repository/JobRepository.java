package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByWorkerIdAndIsWorking(Long workerId, Boolean isWorking);

    //근무 확인
    @Query("SELECT COUNT(j) > 0 " +
            "FROM Job j " +
            "WHERE j.worker.id = :workerId " +
            "AND j.application.jobPost.employer.id = :employerId")
    boolean existsByWorkerAndEmployer(@Param("workerId") Long workerId,
                                      @Param("employerId") Long employerId);


    boolean existsByWorker_IdAndApplication_Id(Long workerId, Long applicationId);
}
