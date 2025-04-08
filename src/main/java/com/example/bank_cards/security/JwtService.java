package com.example.bank_cards.security;

import com.example.bank_cards.entity.UserEntity;
import io.jsonwebtoken.Claims;

import java.util.Date;

public interface JwtService {

    String generateToken(UserEntity user);

    Boolean isTokenValid(String token);

    String getUsernameFromToken(String token);

    Date extractExpiration(String token);

    Boolean isTokenExpired(String token);

    <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver);

    Claims extractAllClaims(String token);

    String getRoleFromToken(String token);
}
