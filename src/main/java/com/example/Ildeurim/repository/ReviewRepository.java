package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
