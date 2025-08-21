package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 파생 쿼리: Review.employer.id 로 카운트
    long countByEmployerId(Long employerId);
}
