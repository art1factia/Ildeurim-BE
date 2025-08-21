package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.Employer;
import com.example.Ildeurim.domain.JobPost;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
    Optional<Employer> findByPhoneNumber(String phone);

    @Query("select e.id from Employer e where e.phoneNumber = :phoneNumber")
    Optional<Long> findIdByPhoneNumber(@NotBlank String phoneNumber);

    boolean existsByPhoneNumber(@NotBlank String phoneNumber);
}
