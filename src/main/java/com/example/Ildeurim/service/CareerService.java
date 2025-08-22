package com.example.Ildeurim.service;

import com.example.Ildeurim.auth.AuthContext;
import com.example.Ildeurim.commons.enums.UserType;
import com.example.Ildeurim.domain.Career;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.dto.career.CareerRes;
import com.example.Ildeurim.dto.worker.WorkerDetailRes;
import com.example.Ildeurim.repository.CareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerService {
    private final CareerRepository careerRepository;
    //TODO: create 메서드 작성

    //TODO: get 메서드 작성 - worker가 갖는 career들을 List<CareerRes>로 반환
    @Transactional(readOnly = true)
    public List<CareerRes> getWorkerCareerList() {
        Long id = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));
        UserType userType = AuthContext.userType()
                .orElseThrow(() -> new AccessDeniedException("Invalid userType"));
        if (userType.equals(UserType.WORKER)) {
            List<Career> careerList = careerRepository.findByWorker_Id(id);
            return careerList.stream()
                    .map(CareerRes::from).toList();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user type is not worker");
        }
    }

    //TODO: get 메서드 작성 - employer가 특정 workerId에 대해 공개된 career들을 List<CareerRes>로 반환
    @Transactional(readOnly = true)
    public List<CareerRes> getWorkerPublicCareerList(Long id) {
        Long userId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));
        UserType userType = AuthContext.userType()
                .orElseThrow(() -> new AccessDeniedException("Invalid userType"));
        if (userType.equals(UserType.EMPLOYER)) {
            List<Career> publicCareerList = careerRepository.findByWorker_IdAndIsOpening(id, true);
            return publicCareerList.stream()
                    .map(CareerRes::from).toList();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user type is not employer");
        }

    }

    //TODO: update 메서드 작성

    //TODO: delete 메서드 작성
}
