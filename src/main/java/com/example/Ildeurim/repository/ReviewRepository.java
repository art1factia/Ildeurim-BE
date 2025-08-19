package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
