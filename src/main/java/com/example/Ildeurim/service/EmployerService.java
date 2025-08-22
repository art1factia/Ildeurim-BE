package com.example.Ildeurim.service;

import com.example.Ildeurim.auth.AuthContext;
import com.example.Ildeurim.auth.CustomPrincipal;
import com.example.Ildeurim.command.EmployerUpdateCmd;
import com.example.Ildeurim.command.WorkerUpdateCmd;
import com.example.Ildeurim.commons.enums.UserType;
import com.example.Ildeurim.domain.Employer;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.dto.employer.*;
import com.example.Ildeurim.dto.worker.*;
import com.example.Ildeurim.jwt.JwtUtil;
import com.example.Ildeurim.mapper.EmployerUpdateCmdMapper;
import com.example.Ildeurim.mapper.JobFieldMapper;
import com.example.Ildeurim.mapper.WorkerUpdateCmdMapper;
import com.example.Ildeurim.repository.EmployerRepository;
import com.example.Ildeurim.repository.ReviewRepository;
import com.example.Ildeurim.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployerService {
    private final EmployerRepository employerRepository;
    private final ReviewRepository reviewRepository;
    private final JwtUtil jwtUtil;
    private final WorkerRepository workerRepository;
    private final JobFieldMapper jobFieldMapper;
    private final EmployerUpdateCmdMapper employerUpdateCmdMapper;

    /**
     * 가입 전용 토큰(scope=signup, userType=Employer)으로만 호출되어야 함.
     * principal.phone()을 신뢰해 Employer 생성하고, access 토큰을 발급해 반환.
     */
    @Transactional
    public EmployerSignupRes signup(CustomPrincipal principal, EmployerCreateReq req) {
        // 1) 토큰 검증: WORKER 가입인지 확인
        if (principal == null || principal.userType() != UserType.EMPLOYER ) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong user type for employer signup");
        }
        if (! principal.scope().equals("signup")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token scope is not in signup");
        }

        // 2) 이미 존재하는지 확인 (이중확인)
        String phone = principal.phone();
        if (employerRepository.existsByPhoneNumber(phone)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employer already exists");
        }
        Employer employer = req.toEntity();
        employerRepository.save(employer);

        // 4) Access 토큰 발급 (ROLE_WORKER 포함)
        String accessToken = jwtUtil.generateAccessToken(employer.getId(), UserType.WORKER, phone, 60);
        long expEpochSec = jwtUtil.getExpiresAtEpochSeconds(accessToken);

        return new EmployerSignupRes(employer.getId(), accessToken, expEpochSec);
    }

    @Transactional(readOnly = true)
    public EmployerDetailRes me() {
        Long id = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));

        UserType userType = AuthContext.userType()
                .orElseThrow(() -> new AccessDeniedException("Invalid userType"));

        if (userType.equals(UserType.EMPLOYER)) {
            Employer employer = employerRepository.findById(id)
                    .get();
            long reviewCount = reviewRepository.countByEmployerId(id);
            //TODO: JSON(questionList) -> 추후 상세 구현
            return EmployerDetailRes.from(employer, reviewCount, new JSONObject());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user type is not employer");
        }
    }

    @Transactional
    public EmployerRes update(EmployerUpdateReq req){
        Long id = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("Unauthenticated"));
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Employer not found"));
        EmployerUpdateCmd cmd = employerUpdateCmdMapper.toCmd(req, jobFieldMapper);
        employer.update(cmd);
        employer = employerRepository.save(employer);
        return EmployerRes.from(employer);
    }




}