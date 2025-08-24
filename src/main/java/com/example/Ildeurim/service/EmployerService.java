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
import jakarta.persistence.EntityNotFoundException;
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
        // 1) 토큰 검증: Employer 가입인지 확인
        if (principal == null || principal.userType() != UserType.EMPLOYER ) {
            throw new AccessDeniedException("고용주 가입에 맞지 않는 사용자 유형입니다.");
        }
        if (! principal.scope().equals("signup")){
            throw new AccessDeniedException("토큰 범위가 가입용이 아닙니다.");
        }

        // 2) 이미 존재하는지 확인 (이중확인)
        String phone = principal.phone();
        if (employerRepository.existsByPhoneNumber(phone)) {
            throw new IllegalArgumentException("이미 존재하는 고용주입니다.");
        }
        Employer employer = req.toEntity();
        employerRepository.save(employer);

        // 4) Access 토큰 발급 (ROLE_WORKER 포함)
        String accessToken = jwtUtil.generateAccessToken(employer.getId(), UserType.EMPLOYER, phone, 360);
        long expEpochSec = jwtUtil.getExpiresAtEpochSeconds(accessToken);

        return new EmployerSignupRes(employer.getId(), accessToken, expEpochSec);
    }

    @Transactional(readOnly = true)
    public EmployerDetailRes me() {
        Long id = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));

        UserType userType = AuthContext.userType()
                .orElseThrow(() -> new AccessDeniedException("사용자 유형이 유효하지 않습니다."));

        if (userType.equals(UserType.EMPLOYER)) {
            Employer employer = employerRepository.findById(id)
                    .get();
            long reviewCount = reviewRepository.countByEmployerId(id);
            //TODO: JSON(questionList) -> 추후 상세 구현
            return EmployerDetailRes.from(employer, reviewCount, new JSONObject());
        } else {
            throw new IllegalArgumentException("고용주가 아닙니다.");
        }
    }

    @Transactional
    public EmployerRes update(EmployerUpdateReq req){
        Long id = AuthContext.userId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 고용주를 찾을 수 없습니다."));
        EmployerUpdateCmd cmd = employerUpdateCmdMapper.toCmd(req, jobFieldMapper);
        employer.update(cmd);
        employer = employerRepository.save(employer);
        return EmployerRes.from(employer);
    }

//


}