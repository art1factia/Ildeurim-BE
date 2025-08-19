package com.example.Ildeurim.service;

import com.example.Ildeurim.domain.Employer;
import com.example.Ildeurim.repository.EmployerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployerService {
    private final EmployerRepository employerRepository;

    @Transactional
    public Long ensureByPhone(String e164Phone) {
        return employerRepository.findByPhone(e164Phone)
                .map(Employer::getId)
                .orElseGet(() -> {
                    Employer e = new Employer();
                    e.setPhoneNumber(e164Phone);
                    try {
                        return employerRepository.save(e).getId();
                    } catch (DataIntegrityViolationException dup) {
                        return employerRepository.findByPhone(e164Phone)
                                .map(Employer::getId)
                                .orElseThrow(() -> dup);
                    }
                });
    }
}