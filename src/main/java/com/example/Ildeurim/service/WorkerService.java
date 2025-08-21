package com.example.Ildeurim.service;

import com.example.Ildeurim.auth.AuthContext;
import com.example.Ildeurim.auth.CustomPrincipal;
import com.example.Ildeurim.command.WorkerUpdateCmd;
import com.example.Ildeurim.commons.enums.UserType;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.dto.employer.EmployerDetailRes;
import com.example.Ildeurim.dto.worker.*;
import com.example.Ildeurim.jwt.JwtUtil;
import com.example.Ildeurim.mapper.JobFieldMapper;
import com.example.Ildeurim.mapper.WorkPlaceMapper;
import com.example.Ildeurim.mapper.WorkerUpdateCmdMapper;
import com.example.Ildeurim.repository.ApplicationRepository;
import com.example.Ildeurim.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    /**
     * 가입 전용 토큰(scope=signup, userType=WORKER)으로만 호출되어야 함.
     * principal.phone()을 신뢰해 Worker를 생성하고, access 토큰을 발급해 반환.
     */
    @Transactional
    public WorkerSignupRes signup(CustomPrincipal principal, WorkerCreateReq req) {
        // 1) 토큰 검증: WORKER 가입인지 확인
        if (principal == null || principal.userType() != UserType.WORKER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong user type for worker signup");
        }
        if (! principal.scope().equals("signup")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token scope is not in signup");
        }

        // 2) 이미 존재하는지 확인 (이중확인)
        String phone = principal.phone();
        if (workerRepository.existsByPhoneNumber(phone)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Worker already exists");
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
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));
        Worker worker = workerRepository.findById(id)
                .get();
        //TODO: List<ApplicationRes>, List<JobRes> 붙여서 DTO 만들기
        long applicationCount = applicationRepository.countByWorkerId(id);
        return WorkerDetailRes.from(worker, applicationCount);
    }

    //TODO: update service 구현
    @Transactional
    public WorkerRes update(WorkerUpdateReq req){
        Long id = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));
        Worker worker = workerRepository.findById(id)
                .get();
        WorkerUpdateCmd cmd = workerUpdateCmdMapper.toCmd(req, jobFieldMapper, workPlaceMapper);
        worker.update(cmd);
        worker = workerRepository.save(worker);
        return WorkerRes.from(worker);
    }
}