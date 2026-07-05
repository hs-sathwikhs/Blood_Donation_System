package com.blooddonation.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {
    private final SecretKey key;

    public JwtUtils(@Value("${JWT_SECRET:}") String secret) {
        // a local session ends on restart unless a secret is configured
        key = secret.isBlank() ? Keys.secretKeyFor(SignatureAlgorithm.HS256)
                : Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String generateToken(String id, String role) {
        long now = System.currentTimeMillis();
        return Jwts.builder().setSubject(id).claim("role", role)
                .setIssuedAt(new Date(now)).setExpiration(new Date(now + 7_200_000))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }
}
