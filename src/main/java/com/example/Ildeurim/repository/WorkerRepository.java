package com.example.Ildeurim.repository;

import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.domain.Worker;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    Optional<Worker> findByPhoneNumber(String phone);

    @Query("select w.id from Worker w where w.phoneNumber = :phoneNumber")
    Optional<Long> findIdByPhoneNumber(@NotBlank String phoneNumber);

    boolean existsByPhoneNumber(@NotBlank String phoneNumber);
}
