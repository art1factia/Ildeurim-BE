// 네, 이 파일 맞습니다. CORS 허용만 아래처럼 추가해 주세요.
package com.example.Ildeurim.config;

import com.example.Ildeurim.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // ★ 추가
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// ★ 추가: CORS 설정
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // ★ CORS 활성화
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                        // ★ 프리플라이트 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/health/**").permitAll()
                        .requestMatchers("/debug/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()              // OTP 요청/검증
                        .requestMatchers("/workers/signup").hasAuthority("SCOPE_signup")
                        .requestMatchers("/employers/signup").hasAuthority("SCOPE_signup") // 가입 단계 API
                        .anyRequest().hasAnyRole("WORKER","EMPLOYER")          // 나머지는 정상 접근 토큰 필요
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // ★ 추가: 로컬 개발 Origin과 헤더/메서드 허용
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // 자격증명(쿠키/인증) 허용 시, Origin은 반드시 구체적이거나 패턴이어야 합니다.
        cfg.setAllowCredentials(true);

        // ★ Origin은 '정확한 값' 또는 패턴으로 제한 (와일드카드 '*' 금지 when allowCredentials=true)
        cfg.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "https://*.nip.io"         // 예: https://app.81.123.168.184.nip.io 허용
                // 필요 시 실제 배포 도메인 추가
        ));

        // ★ 메서드/헤더는 폭넓게 허용 (사전요청 403 방지)
        cfg.addAllowedMethod("*");
        cfg.addAllowedHeader("*");     // ← token 포함, 기타 커스텀 헤더/프레임워크 헤더도 모두 허용

        // 응답에서 노출할 헤더(브라우저 JS가 읽을 수 있게)
        cfg.setExposedHeaders(List.of("token","Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

}
