package com.tranquility.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JWTUtil {

    @Value("${spring.security.oauth2.client.registration.speaksmart.jwt-secret}")
    private final String SECRET_KEY;

    public String generateToken(String username) {  // only using username to encode inside jwt token
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ","JWT")
                .and()
                .issuedAt(new Date(now))
                .expiration(new Date(now + 1000 * 60 * 60 * 24 * 7)) // 1 week expiration time
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    // if signature verification fails we get JwtException -> Invalid token Or
    // ExpiredJwtException -> Token has expired
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .clockSkewSeconds(60) // this will allow access 60s before it becomes valid and 60s after its expiry to compensate for clock mismatch b/w issuer & validator server
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}