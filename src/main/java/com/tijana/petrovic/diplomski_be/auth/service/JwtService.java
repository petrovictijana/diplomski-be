package com.tijana.petrovic.diplomski_be.auth.service;

import com.tijana.petrovic.diplomski_be.auth.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;

    public JwtService(JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecretKey()
                        .getBytes(StandardCharsets.UTF_8)
        );

        this.accessTokenExpiration = jwtProperties.getAccessTokenExpiration();
    }

    public String generateAccessToken(UserDetails userDetails) {
        var now = new Date();
        var expiration = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token)
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        var username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
