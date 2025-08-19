package com.example.Ildeurim.jwt;

import com.example.Ildeurim.commons.enums.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long expMin;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expMin:60}") long expMin
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expMin = expMin;
    }

    public String generateToken(Long userId, UserType userType, String phone) {
        Instant now = Instant.now();
        Instant exp = now.plus(expMin, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(String.valueOf(userId))          // sub = userId
                .claim("utype", userType.name())          // 사용자 종류
                .claim("phone", phone)                    // 필요 시
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
    }

    // 편의 메서드들
    public Long getUserId(Jws<Claims> jws) {
        return Long.valueOf(jws.getPayload().getSubject());
    }
    public UserType getUserType(Jws<Claims> jws) {
        String u = jws.getPayload().get("utype", String.class);
        return UserType.valueOf(u);
    }
    public String getPhone(Jws<Claims> jws) {
        return jws.getPayload().get("phone", String.class);
    }
}