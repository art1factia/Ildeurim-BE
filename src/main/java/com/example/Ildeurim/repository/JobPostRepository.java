package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.JobPost;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {

    List<JobPost> findByEmployer_Id(Long employerId);

    List<JobPost> findAll(Specification<JobPost> spec, Sort startDate);
}
