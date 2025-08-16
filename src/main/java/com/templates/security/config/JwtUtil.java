package com.templates.security.config;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 soat
    private final long refreshTokenExpiration = 7 * 24 * 60 * 60 * 1000;

    public String createAccessToken(String subject, Integer userId) {
        //
        return Jwts.builder()
                .subject(subject)
                .claim("userId", userId)
                .claim("type", "AccessToken")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String createRefreshToken(String subject, Integer userId) {

        return Jwts.builder()
                .subject(subject)
                .claim("userId", userId)
                .claim("type", "RefreshToken")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public <T> T getClaim(
            @NonNull String name,
            @NonNull String token,
            @NonNull Class<T> type
    ) {
        try {
            return extractAllClaims (token)
                    .get (name, type);
        } catch (Exception e) {
            return null;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        System.out.println(Objects.isNull(token));
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, String username) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY) // server secret
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String tokenUsername = claims.getSubject();
            return (tokenUsername.equals(username) && !claims.getExpiration().before(new Date()));
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token){
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY) // bu yerda serverdagi secret yoki public key bo‘ladi
                    .build()
                    .parseClaimsJws(token); // bu yerda signature ham, expiry ham tekshiriladi
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

}
