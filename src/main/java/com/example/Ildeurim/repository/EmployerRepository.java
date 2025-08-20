package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.Employer;
import com.example.Ildeurim.domain.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
}
