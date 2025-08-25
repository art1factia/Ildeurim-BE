package com.example.Ildeurim.repository;

import com.example.Ildeurim.commons.enums.application.ApplicationStatus;
import com.example.Ildeurim.domain.Application;
import com.example.Ildeurim.domain.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByWorkerId(Long workerId);

    List<Application> findByJobPost(JobPost jobPost);
    long countByWorkerId(Long workerId);

    boolean existsByWorkerIdAndJobPostIdAndApplicationStatusIn(
            Long workerId,
            Long jobPostId,
            List<ApplicationStatus> statuses
    );

    List<Application> findByJobPost_IdAndApplicationStatus(Long jobPostId, ApplicationStatus applicationStatus);

    List<Application> findByJobPost_IdAndApplicationStatusNot(Long jobPostId, ApplicationStatus applicationStatus);

    List<Application> findByJobPost_IdAndApplicationStatusIsNotAndApplicationStatusIsNot(Long jobPostId, ApplicationStatus applicationStatus, ApplicationStatus applicationStatus1);

    boolean existsByWorker_Id(Long workerId);

    boolean existsByWorker_IdAndJobPost_Id(Long workerId, Long jobPostId);

    boolean existsByWorker_IdAndJob_Id(Long workerId, Long jobId);

    Optional<Application> findByWorker_IdAndJob_Id(Long workerId, Long jobId);

    Optional<Application> findByJobPost_Id(Long jobPostId);

}
