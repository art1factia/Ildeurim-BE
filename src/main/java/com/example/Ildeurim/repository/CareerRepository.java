package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.Career;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Long> {
    List<Career> findByWorker_Id(Long workerId);

    List<Career> findByWorker_IdAndIsOpening(Long workerId, Boolean isOpening);
}
