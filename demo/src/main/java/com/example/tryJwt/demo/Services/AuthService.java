package com.example.tryJwt.demo.Services;

import com.example.tryJwt.demo.FileRequest.LoginRequest;
import com.example.tryJwt.demo.FileRequest.RegisterRequest;
import com.example.tryJwt.demo.FileRequest.TokenResponse;
import com.example.tryJwt.demo.Modelo.Token;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.tryJwt.demo.Repository.TokenRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
@PropertySource("classpath:application.properties")
public class AuthService {

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  TokenRepository tokenRepository;

    @Autowired
    private  JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${jwt.token.registration}")
    private boolean tokenRegistration;


    public TokenResponse register(RegisterRequest request) {
        var user = new Users();
        // user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        var saveUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(saveUser,jwtToken);
        return new TokenResponse(jwtToken,refreshToken);
    }

    public TokenResponse login (LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = userRepository.findByEmail(request.email()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokedAllUserTokens(user);
        saveUserToken(user,jwtToken);
        return new TokenResponse(jwtToken,refreshToken);
    }

    public ResponseEntity<TokenResponse> refreshToken(String authHeader) {
        String userEmail;
        userEmail = jwtService.extractEmail(authHeader);
        if(userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String finalUserEmail = userEmail;
        Users user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new UsernameNotFoundException(finalUserEmail));
        if(!jwtService.isValidToken(authHeader,user)) {
            throw new IllegalArgumentException("Invalid Refresh Token");
        }
        String accessToken = jwtService.generateToken(user);
        revokedAllUserTokens(user);
        saveUserToken(user,accessToken);
        return  ResponseEntity.ok(new TokenResponse(accessToken, jwtService.normalizeToken(authHeader)));
    }

    private void saveUserToken(Users user, String jwtToken) {
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

    private void revokedAllUserTokens(Users users) {
        if(tokenRegistration) {
            List<Token> validUserTokens = tokenRepository.findAllValidIsFalseOrRevokedIsFalseByUserId(users.getId());
            if (!validUserTokens.isEmpty()) {
                for (Token token : validUserTokens) {
                    token.setExpired(true);
                    token.setRevoked(true);
                }
                tokenRepository.saveAll(validUserTokens);
            }
        }
    }
    public Integer validate(String token) {
        if(jwtService.isTokenExpired(token))
        {
            return 1;
        }
        return 0;
    }
}
