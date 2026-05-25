package com.example.tryJwt.demo.Services;

import com.example.tryJwt.demo.Modelo.Token;
import com.example.tryJwt.demo.Modelo.Usuario;
import com.example.tryJwt.demo.Repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.Optional;


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

    @Value("${jwt.token.registration}")
    private boolean tokenRegistration;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    public String getAccessToken(String refreshToken, Usuario usuario) {
        // Para generar accessToken el refresh debe pertenecer el user
        if(extractEmail(refreshToken).equals(usuario.getEmail()))
            return buildToken(usuario, expirationAccessToken);
        else
            throw new JwtException("Invalid Refresh Token");
    }

    public String getRefreshToken(Usuario user) {
        if(tokenRegistration) {
            Optional<Token> foundToken = refreshTokenRepository.findByUserId(user.getId());
            // Si existe y no esta expirado, retorno ese refreshToken
            if (foundToken.isPresent() && !isTokenExpired(foundToken.get().getToken())) {
                return foundToken.get().getToken();
            //Si existe pero esta expirado lo revoco
            } else if (foundToken.isPresent() && isTokenExpired(foundToken.get().getToken())) {
                revokeUserRefreshToken(user);
            }
            // Si esta revocado o no fue encontrado, genero uno nuevo y lo persisto
            Token token = Token.builder()
                    //.tokenType(Token.TokenType.BEARER)
                    .token(buildToken(user, expirationRefreshToken))
                    .user(user)
                    .build();
            refreshTokenRepository.save(token);
            return token.getToken();
        } else {
            // Si no esta habilitado el registro de tokens devuelvo uno nuevo siempre
            return buildToken(user, expirationRefreshToken);
        }
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
            if(token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("Token inválido o mal formado", ex);
        }
    }

    @Deprecated
    public String extractUsername(String token) {
        try {
            if(token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return (String) claims.get("name");
        } catch (JwtException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("Token inválido o mal formado", ex);
        }
    }

    @Deprecated
    public String normalizeToken(String token) {
        if(token== null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token invalido");
        }
        return token.substring(7);
    }

    public boolean isTokenExpired(String token) {
        return extractExpirationToken(token).before(new Date());
    }

    private Date extractExpirationToken(String token) {
        try {
            if(token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("Token inválido o mal formado", ex);
        }
    }

    @Deprecated
    private void saveUserToken(Usuario user, String jwtToken) {
        if(tokenRegistration) {
            Token token = new Token();
            token.setUser(user);
            token.setToken(jwtToken);
            //token.setTokenType(Token.TokenType.BEARER);
            refreshTokenRepository.save(token);
        }
    }

    public void revokeUserRefreshToken(Usuario usuario) {
        if(tokenRegistration) {
            refreshTokenRepository.deleteByUserId(usuario.getId());
        }
    }

}