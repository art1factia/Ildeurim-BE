package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.domain.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    Optional<Worker> findByPhone(String phone);
    boolean existsByPhone(String phone);
}
