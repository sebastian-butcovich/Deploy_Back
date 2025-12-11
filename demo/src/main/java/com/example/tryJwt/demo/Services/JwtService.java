package com.example.tryJwt.demo.Services;

import com.example.tryJwt.demo.Modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;


@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class JwtService {

    @Value("${jwt.access.token.expiration}")
    private long expirationAccessToken;

    @Value("${jwt.refresh.token.expiration}")
    private long expirationRefreshToken;

    @Value("${jwt.secret.key}")
    private String secretKey;


    public String generateAccessToken(Usuario user)
    {
        return buildToken(user,expirationAccessToken);
    }

    public String generateRefreshToken(Usuario user)
    {
        return buildToken(user,expirationRefreshToken);
    }

    private String buildToken(Usuario user, long expiration) {
        return Jwts.builder().id(user.getId().toString())
                .claims(Map.of("name",user.getEmail()))
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBates = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBates);
    }

    public String extractEmail(String token) {
        try {
            String jwt = normalizeToken(token);
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("Token inválido o mal formado", ex);
        }
    }

    public String extractUsername(String token) {
        try {
            String jwt = normalizeToken(token);
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
            return (String) claims.get("name");
        } catch (JwtException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("Token inválido o mal formado", ex);
        }
    }

    public String normalizeToken(String token) {
        if(token== null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token invalido");
        }
        return token.substring(7);
    }

    public boolean isValidToken(String token, Usuario user) {
        String userEmail = extractEmail(token);
        return (userEmail.equals(user.getEmail()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpirationToken(token).before(new Date());
    }

    private Date extractExpirationToken(String token) {
        try {
            String jwt = normalizeToken(token);
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
            return claims.getExpiration();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("Token inválido o mal formado", ex);
        }
    }

}