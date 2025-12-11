package com.example.tryJwt.demo.Services;

import com.example.tryJwt.demo.FileRequest.ChangePasswordRequest;
import com.example.tryJwt.demo.FileRequest.LoginRequest;
import com.example.tryJwt.demo.FileRequest.UsuarioDto;
import com.example.tryJwt.demo.FileRequest.TokenResponse;
import com.example.tryJwt.demo.Mapper.RegisterRequestMapper;
import com.example.tryJwt.demo.Modelo.Token;
import com.example.tryJwt.demo.Modelo.Usuario;
import com.example.tryJwt.demo.Repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.tryJwt.demo.Repository.TokenRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
@PropertySource("classpath:application.properties")
public class AuthService {

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private  TokenRepository tokenRepository;

    @Autowired
    private  JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${jwt.token.registration}")
    private boolean tokenRegistration;

    @Autowired
    private RegisterRequestMapper registerRequestMapper;


    @Transactional
    public TokenResponse register(UsuarioDto request) {
        Usuario user = registerRequestMapper.toEntity(request);
        user.setCreado(new Date());
        user.setUltimaModificacion(new Date());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setId(null);
        Usuario saveUser = usuarioRepository.save(user);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(saveUser,refreshToken);
        return new TokenResponse(accessToken,refreshToken);
    }

    @Transactional
    public TokenResponse login (LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        Optional<Usuario> user = usuarioRepository.findByEmail(request.email());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
        String accessToken = jwtService.generateAccessToken(user.get());
        if(tokenRegistration) {
            Optional<Token> savedRefreshToken = tokenRepository.findValidIsFalseOrRevokedIsFalseByUserId(user.get().getId());
            if (savedRefreshToken.isEmpty() || !jwtService.isValidToken(savedRefreshToken.get().getToken(), user.get())) {
                revokeUserRefreshToken(user.get());
                String newRefreshToken = jwtService.generateRefreshToken(user.get());
                saveUserToken(user.get(), newRefreshToken);
                return new TokenResponse(accessToken, newRefreshToken);
            } else {
                return new TokenResponse(accessToken, savedRefreshToken.get().getToken());
            }
        } else {
            return new TokenResponse(accessToken, jwtService.generateRefreshToken(user.get()));
        }
    }

    @Transactional
    public TokenResponse refreshAccessToken(String expiredAccessToken, String refreshToken) {
        String userEmail = jwtService.extractEmail(expiredAccessToken);
        Optional<Usuario> user = usuarioRepository.findByEmail(userEmail);
        if(user.isEmpty()) {
            throw new UsernameNotFoundException("Username not found with email: " + userEmail);
        }
        if(!jwtService.isValidToken(expiredAccessToken,user.get())) {
            throw new IllegalArgumentException("Invalid access token");
        }
        if(!jwtService.isValidToken(refreshToken,user.get())) {
            throw new IllegalArgumentException("Invalid refresh token. Must login again");
        }
        if(!userEmail.equals(jwtService.extractEmail(refreshToken))) {
            throw new IllegalArgumentException("Refresh token owner does not correspond with access token owner");
        }
        String newAccessToken = jwtService.generateAccessToken(user.get());
        return new TokenResponse(newAccessToken, refreshToken);
    }

    @Transactional
    public void changePassword(String token, ChangePasswordRequest req) {
        Usuario me = usuarioRepository.findByEmail(jwtService.extractEmail(token))
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el elemento con token: " + token));
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!me.getPassword().equals(encoder.encode(req.repeatNewPassword()))) {
            throw new IllegalArgumentException("La contraseña antigua no coincide con la provista");
        }
        if(!req.newPassword().equals(req.repeatNewPassword())) {
            throw new IllegalArgumentException("La contraseñas no coinciden");
        }
        me.setPassword(encoder.encode(req.newPassword()));
        usuarioRepository.save(me);
    }

    private void saveUserToken(Usuario user, String jwtToken) {
        if(tokenRegistration) {
            Token token = new Token();
            token.setUser(user);
            token.setToken(jwtToken);
            token.setTokenType(Token.TokenType.BEARER);
            token.setRevoked(false);
            token.setExpired(false);
            tokenRepository.save(token);
        }
    }

    private void revokeUserRefreshToken(Usuario usuario) {
        if(tokenRegistration) {
            Optional<Token> token = tokenRepository.findValidIsFalseOrRevokedIsFalseByUserId(usuario.getId());
            if (token.isPresent()) {
                token.get().setExpired(true);
                token.get().setRevoked(true);
                tokenRepository.save(token.get());
            }
        }
    }

    public Integer validate(String token) {
        if(jwtService.isTokenExpired(token)) {
            return 1;
        }
        return 0;
    }
}