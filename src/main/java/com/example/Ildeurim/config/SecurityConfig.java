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

        // 프론트 개발 도메인/포트
        cfg.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5176",
                "http://localhost:5177",
                // 배포(또는 테스트) 도메인 필요 시 추가
                "https://app.184.168.123.81.nip.io"
        ));

        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("token","Content-Type"));
        cfg.setExposedHeaders(List.of("token"));
        cfg.setAllowCredentials(true); // 쿠키/인증정보 사용 시 true

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
