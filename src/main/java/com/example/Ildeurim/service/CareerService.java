package com.example.Ildeurim.service;
import com.example.Ildeurim.commons.converter.DateParsers;
import com.example.Ildeurim.auth.AuthContext;
import com.example.Ildeurim.commons.enums.UserType;
import com.example.Ildeurim.commons.enums.jobpost.JobField;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.domain.Career;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.dto.career.CareerCreateReq;
import com.example.Ildeurim.dto.career.CareerUpdateReq;
import com.example.Ildeurim.dto.career.CareerRes;
import com.example.Ildeurim.exception.career.InvalidDateRangeException;
import com.example.Ildeurim.repository.CareerRepository;
import com.example.Ildeurim.repository.WorkerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerService {

    private final CareerRepository careerRepository;
    private final WorkerRepository workerRepository;

    /* ===================== Create ===================== */
    @Transactional
    public CareerRes create(CareerCreateReq req) {
        Long userId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        UserType userType = AuthContext.userType()
                .orElseThrow(() -> new AccessDeniedException("사용자 유형이 유효하지 않습니다."));

        if (!userType.equals(UserType.WORKER)) {
            throw new AccessDeniedException("구직자가 아니므로 이 작업을 수행할 수 없습니다.");
        }

        // Worker 존재 여부 확인
        Worker worker = workerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 구직자입니다."));

        LocalDate startDate = DateParsers.parseLocalDate(req.startDate());
        LocalDate endDate = DateParsers.parseLocalDate(req.endDate());
        // 날짜 순서 검증
        validateDates(startDate, endDate);

        Career career = Career.builder()
                .title(req.title())
                .companyName(req.companyName())
                .startDate(startDate)
                .endDate(endDate)
                .workplace(WorkPlace.fromLabel(req.workplace()))
                .mainDuties(req.mainDuties())
                .isOpening(req.isOpening())
                .jobField(JobField.fromLabel(req.jobField()))
                .worker(worker)
                .build();

        return CareerRes.from(careerRepository.save(career));
    }

    /* ===================== Get ===================== */
    @Transactional(readOnly = true)
    public List<CareerRes> getWorkerCareerList() {
        Long id = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        UserType userType = AuthContext.userType()
                .orElseThrow(() -> new AccessDeniedException("사용자 유형이 유효하지 않습니다."));
        if (userType.equals(UserType.WORKER)) {
            List<Career> careerList = careerRepository.findByWorker_Id(id);
            return careerList.stream()
                    .map(CareerRes::from)
                    .toList(); // JDK 17 지원
        } else {
            throw new AccessDeniedException("구직자가 아니므로 이 작업을 수행할 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<CareerRes> getWorkerPublicCareerList(Long id) {
        Long userId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        UserType userType = AuthContext.userType()
                .orElseThrow(() -> new AccessDeniedException("사용자 유형이 유효하지 않습니다."));
        if (userType.equals(UserType.EMPLOYER)) {
            List<Career> publicCareerList = careerRepository.findByWorker_IdAndIsOpening(id, true);
            return publicCareerList.stream()
                    .map(CareerRes::from)
                    .toList();
        } else {
            throw new AccessDeniedException("고용주가 아니므로 이 작업을 수행할 수 없습니다.");
        }
    }

    /* ===================== Update ===================== */
    @Transactional
    public CareerRes update(Long id, CareerUpdateReq req) {
        Long userId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        UserType userType = AuthContext.userType()
                .orElseThrow(() -> new AccessDeniedException("사용자 유형이 유효하지 않습니다."));

        if (!userType.equals(UserType.WORKER)) {
            throw new AccessDeniedException("구직자가 아니므로 이 작업을 수행할 수 없습니다.");
        }

        Career career = careerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("이력을 찾을 수 없습니다."));

        if (!career.getWorker().getId().equals(userId)) {
            throw new AccessDeniedException("수정할 권한이 없습니다.");
        }

        // 부분 수정 (null 무시)
        if (req.title() != null) career.setTitle(req.title());
        if (req.companyName() != null) career.setCompanyName(req.companyName());
        if (req.startDate() != null) career.setStartDate(DateParsers.parseLocalDate(req.startDate()));
        if (req.endDate() != null) career.setEndDate(DateParsers.parseLocalDate(req.endDate()));
        if (req.workplace() != null) career.setWorkplace(WorkPlace.fromLabelNullable(req.workplace()));
        if (req.mainDuties() != null) career.setMainDuties(req.mainDuties());
        if (req.isOpening() != null) career.setIsOpening(req.isOpening());
        if (req.jobField() != null) career.setJobField(JobField.fromLabelNullable(req.jobField()));

        // 머지 후 날짜 순서 검증
        validateDates(career.getStartDate(), career.getEndDate());

        return CareerRes.from(career);
    }

    /* ===================== Delete ===================== */
    @Transactional
    public void delete(Long id) {
        Long userId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않는 사용자입니다."));
        UserType userType = AuthContext.userType()
                .orElseThrow(() -> new AccessDeniedException("사용자 유형이 유효하지 않습니다"));

        if (!userType.equals(UserType.WORKER)) {
            throw new AccessDeniedException("구직자가 아니므로 이 작업을 수행할 수 없습니다.");
        }

        Career career = careerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("이력을 찾을 수 없습니다."));

        if (!career.getWorker().getId().equals(userId)) {
            throw new AccessDeniedException("이력을 삭제할 수 없습니다.");
        }

        careerRepository.delete(career);
    }

    /* ===================== Helper ===================== */
    private void validateDates(LocalDate start, LocalDate end) {
        // null 여부는 DB/DTO(@NotNull)로 보장된다고 가정하고 순서만 검사
        if (end.isBefore(start)) {
            throw new InvalidDateRangeException(start,end,"종료일이 시작일보다 앞설 수 없습니다.");
        }
    }
}
