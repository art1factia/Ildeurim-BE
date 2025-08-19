package com.example.Ildeurim.jwt;

import com.example.Ildeurim.auth.CustomPrincipal;
import com.example.Ildeurim.auth.CustomUserDetails;
import com.example.Ildeurim.commons.enums.UserType;
import com.example.Ildeurim.domain.Employer;
import com.example.Ildeurim.domain.Worker;
import com.example.Ildeurim.repository.EmployerRepository;
import com.example.Ildeurim.repository.WorkerRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final WorkerRepository workerRepository;
    private final EmployerRepository employerRepository;

    public JwtAuthFilter(JwtUtil jwtUtil,
                         WorkerRepository workerRepository,
                         EmployerRepository employerRepository) {
        this.jwtUtil = jwtUtil;
        this.workerRepository = workerRepository;
        this.employerRepository = employerRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String auth = req.getHeader("Authorization");

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Jws<Claims> jws = jwtUtil.parse(token);
                Long userId = jwtUtil.getUserId(jws);
                UserType userType = jwtUtil.getUserType(jws);
                String phone = jwtUtil.getPhone(jws);

                // 레포지토리에서 존재 확인
                boolean exists = switch (userType) {
                    case WORKER   -> workerRepository.existsById(userId);
                    case EMPLOYER -> employerRepository.existsById(userId);
                };
                if (!exists) {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Account not found or inactive");
                    return;
                }

                var principal = new CustomPrincipal(userId, userType, phone);
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + userType.name()));
                var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                // SecurityContext 세팅
                org.springframework.security.core.context.SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

            } catch (Exception e) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        }

        chain.doFilter(req, res);
    }
}