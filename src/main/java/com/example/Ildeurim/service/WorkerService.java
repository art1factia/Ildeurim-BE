package com.example.Ildeurim.service;

import com.example.Ildeurim.auth.AuthContext;
import com.example.Ildeurim.auth.CustomPrincipal;
import com.example.Ildeurim.command.WorkerUpdateCmd;
import com.example.Ildeurim.commons.enums.UserType;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.dto.employer.EmployerDetailRes;
import com.example.Ildeurim.dto.worker.*;
import com.example.Ildeurim.jwt.JwtUtil;
import com.example.Ildeurim.mapper.DateMapper;
import com.example.Ildeurim.mapper.JobFieldMapper;
import com.example.Ildeurim.mapper.WorkPlaceMapper;
import com.example.Ildeurim.mapper.WorkerUpdateCmdMapper;
import com.example.Ildeurim.repository.ApplicationRepository;
import com.example.Ildeurim.repository.WorkerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkerService {
    private final WorkerRepository workerRepository;
    private final ApplicationRepository applicationRepository;
    private final JwtUtil jwtUtil;
    private final WorkerUpdateCmdMapper workerUpdateCmdMapper;
    private final JobFieldMapper jobFieldMapper;
    private final WorkPlaceMapper workPlaceMapper;
    private final DateMapper dateMapper;
    private final ObjectStorageService storage;
    /**
     * 가입 전용 토큰(scope=signup, userType=WORKER)으로만 호출되어야 함.
     * principal.phone()을 신뢰해 Worker를 생성하고, access 토큰을 발급해 반환.
     */
    @Transactional
    public WorkerSignupRes signup(CustomPrincipal principal, WorkerCreateReq req) {
//        System.out.println(principal);
        // 1) 토큰 검증: WORKER 가입인지 확인
        if (principal == null || principal.userType() != UserType.WORKER) {
//            System.out.println(principal);
            throw new AccessDeniedException("구직자 가입에 맞지 않는 사용자 유형입니다.");
        }
        if (!principal.scope().equals("signup")) {
//            System.out.println(principal);
            throw new AccessDeniedException("토큰 범위가 가입용이 아닙니다.");
        }

        // 2) 이미 존재하는지 확인 (이중확인)
        String phone = principal.phone();
        if (workerRepository.existsByPhoneNumber(phone)) {
            throw new IllegalArgumentException("이미 존재하는 구직자입니다.");
        }
        Worker worker = req.toEntity();
        workerRepository.save(worker);

        // 4) Access 토큰 발급 (ROLE_WORKER 포함)
        String accessToken = jwtUtil.generateAccessToken(worker.getId(), UserType.WORKER, phone, 60);
        long expEpochSec = jwtUtil.getExpiresAtEpochSeconds(accessToken);

        return new WorkerSignupRes(worker.getId(), accessToken, expEpochSec);
    }

    @Transactional(readOnly = true)
    public WorkerDetailRes me() {
        Long id = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));

        UserType userType = AuthContext.userType()
                .orElseThrow(() -> new AccessDeniedException("사용자 유형이 유효하지 앖습니다."));
        if (userType.equals(UserType.WORKER)) {
            Worker worker = workerRepository.findById(id)
                    .get();
            //TODO: List<ApplicationRes>, List<JobRes> 붙여서 DTO 만들기
            long applicationCount = applicationRepository.countByWorkerId(id);
            return WorkerDetailRes.from(worker, applicationCount);
        } else {
            throw new AccessDeniedException("구직자가 아닙니다.");
        }
    }

    //TODO: update service 구현
    @Transactional
    public WorkerRes update(WorkerUpdateReq req) {
        Long id = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new AccessDeniedException("해당 구직자를 찾을 수 없습니다."));
        WorkerUpdateCmd cmd = workerUpdateCmdMapper.toCmd(req, jobFieldMapper, workPlaceMapper, dateMapper);
        worker.update(cmd);
        worker = workerRepository.save(worker);
        return WorkerRes.from(worker);
    }

    @Transactional
    public String updateMyProfileImage(MultipartFile file) {
        Long userId = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        var userType = AuthContext.userType()
                .orElseThrow(() -> new AccessDeniedException("=사용자 유형이 유효하지 않습니다."));
        if (userType != UserType.WORKER)
            throw new AccessDeniedException("구직자 전용");

        Worker w = workerRepository.findById(userId)
                .orElseThrow(() -> new AccessDeniedException("해당 구직자를 찾을 수 없습니다."));

        String newUrl = storage.uploadWorkerProfile(w.getId(), file, w.getProfileImgURL());
        w.setProfileImgURL(newUrl); // 변경 감지
        // save 호출 불필요(JPA flush)지만, 명시적으로 하려면:
        // workerRepository.save(w);
        return newUrl;
    }
}