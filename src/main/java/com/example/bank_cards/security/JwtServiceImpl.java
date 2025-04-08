package com.example.bank_cards.security;

import com.example.bank_cards.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class JwtServiceImpl implements JwtService {

    private final String jwtKey;

    private final Integer sessionTime;

    public JwtServiceImpl(@Value("${token.jwt.key}") String jwtKey,
                          @Value("${token.jwt.expiration}") Integer sessionTime) {
        this.jwtKey = jwtKey;
        this.sessionTime = sessionTime;
    }

    @Override
    public String generateToken(UserEntity user) {
        var dateTime = GregorianCalendar.getInstance();
        var issuedAt = dateTime.getTime();
        dateTime.add(Calendar.MINUTE, sessionTime);
        var expiration = dateTime.getTime();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getAuthorities().iterator().next().getAuthority());

        return "Bearer " + Jwts.builder()
                .issuer("Bank Cards")
                .subject(user.getEmail())
                .claims(claims)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    @Override
    public Boolean isTokenValid(String token) {
        final String username = getUsernameFromToken(token);
        return username != null && !isTokenExpired(token);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public String getRoleFromToken(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
}
