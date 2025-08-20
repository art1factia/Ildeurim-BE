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

    // Access 토큰: 정상 사용용
    public String generateAccessToken(Long userId, UserType userType, String phone, long expMin) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("utype", userType.name())
                .claim("phone", phone)
                .claim("scope", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expMin, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }
    // Signup 토큰: 회원가입 단계에서만 사용 (ROLE 부여 X)
    public String generateSignupToken(UserType userType, String phone, long ttlMin) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject("0") // 아직 userId 없음
                .claim("utype", userType.name())
                .claim("phone", phone)
                .claim("scope", "signup")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttlMin, ChronoUnit.MINUTES))) // 예: 15분
                .signWith(key)
                .compact();
    }
    public String getScope(Jws<Claims> jws) {
        return jws.getPayload().get("scope", String.class);
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

    /** 만들어진 토큰의 exp 클레임을 그대로 읽어서 에포크초로 반환 */
    public long getExpiresAtEpochSeconds(String jwt) {
        var jws = Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(jwt);
        Date exp = jws.getPayload().getExpiration();
        return exp.toInstant().getEpochSecond();
    }
}