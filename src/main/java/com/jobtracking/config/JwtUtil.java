package com.jobtracking.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}") //take from application.properties
    private String secret;

    @Value("${jwt.expiration-ms}") //take from application.properties
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes()); //get the secret key in the form of bytes and create a key for signing the token
    }

    public String generateToken(Long userId, Integer roleId) {
        return Jwts.builder()
                .subject(userId.toString()) //set the subject of the token
                .claim("roleId", roleId) //set the roleId of the token
                .issuedAt(new Date()) //set the issuedAt of the token
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) //set the expiration of the token
                .signWith(getSigningKey()) //sign the token
                .compact(); //compact the token
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}