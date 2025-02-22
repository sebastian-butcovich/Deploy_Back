package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.Modelo.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class JwtService {
    private long expirationToken = 86400000;
    private long expirationRefreshToken = 604800000;
    private String secretKey = "FSDKFJDSFKSJFSDJFDSLKFJDSKLFJDFJCVJCXKVJKJGFDSKFJDFSKLJEWROEW";
    public String generateToken(Users user)
    {
        return buildToken(user,expirationToken);
    }
    public String generateRefreshToken(Users user)
    {
        return buildToken(user,expirationRefreshToken);
    }

    private String buildToken( Users user,long expiration)
    {
        return Jwts.builder().id(user.getId().toString())
                .claims(Map.of("name",user.getName()))
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSingInKey())
                .compact();
    }
    private SecretKey getSingInKey()
    {
        byte[] keyBates = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBates);
    }
    public String extractUsername(String token)
    {
        Claims jwtToken = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return jwtToken.getSubject();

    }
    public boolean isValidToken(String refreshToken, Users user)
    {
        String username = extractUsername(refreshToken);
        return (username.equals(user.getEmail()) && !isTokenExpirated(refreshToken));
    }
    public boolean isTokenExpirated(String token)
    {
        return exctractExpirationToken(token).before(new Date());
    }
    private Date exctractExpirationToken(String token)
    {
        Claims jwtToken = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return jwtToken.getExpiration();
    }

}
