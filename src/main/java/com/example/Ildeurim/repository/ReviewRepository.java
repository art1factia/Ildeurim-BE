package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.Employer;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(value = """
        SELECT AVG(answer) 
        FROM review_answers 
        WHERE review_id IN (
            SELECT id FROM review WHERE employerId = :employerId
        ) AND question = :question
        """, nativeQuery = true)
    Double findAvgByEmployerAndType(@Param("employerId") Long employerId,
                                    @Param("question") String question);

    List<Review> findByEmployer(Employer employer);
}