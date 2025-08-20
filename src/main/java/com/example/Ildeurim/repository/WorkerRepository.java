package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.domain.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
}
