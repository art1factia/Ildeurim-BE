package com.example.Ildeurim.jwt;

import com.example.Ildeurim.auth.CustomPrincipal;
import com.example.Ildeurim.repository.EmployerRepository;
import com.example.Ildeurim.repository.WorkerRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

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
        String auth = req.getHeader("token");
//        System.out.println(auth);

        if (auth != null) {
            String token = auth;
            try {
                var jws = jwtUtil.parse(token);
//                System.out.println("jws:" + jws);
                var scope = jwtUtil.getScope(jws); // "access" | "signup" (기타 값은 무시)
//                System.out.println("scope:" + scope);

                if ("access".equals(scope)) {
                    Long userId = jwtUtil.getUserId(jws);
                    var userType = jwtUtil.getUserType(jws);
                    String phone = jwtUtil.getPhone(jws);

                    // 존재 확인 (isActive 없으면 existsById로 충분)
                    boolean exists = switch (userType) {
                        case WORKER -> workerRepository.existsById(userId);
                        case EMPLOYER -> employerRepository.existsById(userId);
                    };
                    if (!exists) {
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Account not found");
                        return;
                    }

                    // ROLE_* 부여
                    var principal = new CustomPrincipal(userId, userType, phone, scope);
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + userType.name()));
                    seJtAuth(principal, authorities, req);

                } else if ("signup".equals(scope)) {
                    // 가입 단계: ROLE 없이 가입 범위 권한만 부여
                    var principal = new CustomPrincipal(null, jwtUtil.getUserType(jws), jwtUtil.getPhone(jws), scope);
                    var authorities = List.of(new SimpleGrantedAuthority("SCOPE_signup"));
                    seJtAuth(principal, authorities, req);
//                    System.out.println(jwtUtil.getScope(jws));
                }
                // 그 외 scope는 인증 미설정(익명으로 통과)
            } catch (Exception e) {
                // 토큰이 아예 잘못된 경우 즉시 401
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private void seJtAuth(CustomPrincipal principal,
                          List<? extends GrantedAuthority> authorities,
                          HttpServletRequest req) {
        var authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}