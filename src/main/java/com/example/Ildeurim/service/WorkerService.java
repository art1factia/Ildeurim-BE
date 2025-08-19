package com.example.Ildeurim.service;

import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.repository.WorkerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkerService {
    private final WorkerRepository workerRepository;

    @Transactional
    public Long ensureByPhone(String e164Phone) {
        return workerRepository.findByPhone(e164Phone)
                .map(Worker::getId)
                .orElseGet(() -> {
                    Worker w = new Worker();
                    w.setPhoneNumber(e164Phone);
                    try {
                        return workerRepository.save(w).getId();
                    } catch (DataIntegrityViolationException dup) {
                        // 동시요청으로 유니크 충돌 시 재조회
                        return workerRepository.findByPhone(e164Phone)
                                .map(Worker::getId)
                                .orElseThrow(() -> dup);
                    }
                });
    }
}