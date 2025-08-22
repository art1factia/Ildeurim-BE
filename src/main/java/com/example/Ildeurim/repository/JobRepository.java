package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobRepository extends JpaRepository<Job, Long> {

    //근무 확인
    @Query("SELECT COUNT(j) > 0 " +
            "FROM Job j " +
            "WHERE j.worker.id = :workerId " +
            "AND j.application.jobPost.employer.id = :employerId")
    boolean existsByWorkerAndEmployer(@Param("workerId") Long workerId,
                                      @Param("employerId") Long employerId);
}
