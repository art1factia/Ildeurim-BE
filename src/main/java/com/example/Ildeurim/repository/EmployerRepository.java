package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.Employer;
import com.example.Ildeurim.domain.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
    Optional<Employer> findByPhone(String phone);
    boolean existsByPhone(String phone);
}
