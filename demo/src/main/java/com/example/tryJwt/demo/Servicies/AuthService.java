package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.FileRequest.LoginRequest;
import com.example.tryJwt.demo.FileRequest.RegisterRequest;
import com.example.tryJwt.demo.FileRequest.TokenResponse;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Modelo.Token;
import com.example.tryJwt.demo.Repository.TokenRepository;
import com.example.tryJwt.demo.Repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class AuthService {
    @Autowired
  private  PasswordEncoder passwordEncoder;
    @Autowired
  private  UserRepository userRepository;
    @Autowired
  private  TokenRepository tokenRepository;
  @Autowired(required = true)
    private  JwtService jwtService;
  @Autowired
  private AuthenticationManager authenticationManager;
    public TokenResponse register(RegisterRequest request)
    {
        var user = new Users();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFoto(request.foto());
        var saveUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(saveUser,jwtToken);
        return new TokenResponse(jwtToken,refreshToken,request.name(),"");
    }
    public TokenResponse login (LoginRequest request)
    {
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
        return new TokenResponse(jwtToken,refreshToken,user.getName(),user.getFoto());
    }
    public ResponseEntity<TokenResponse> refreshToken(Map<String, String> params)
    {
        String authHeader = params.get("token");

        if(authHeader ==null || !authHeader.startsWith("Bearer "))
        {
            throw new IllegalArgumentException("Invalid Bearer token");
        }
        String refreshToken = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(refreshToken);
        if(userEmail == null )
        {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        Users user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new UsernameNotFoundException(userEmail));
        if(!jwtService.isValidToken(refreshToken,user))
        {
            throw new IllegalArgumentException("Invalid Refresh Token");
        }
        String accessToken = jwtService.generateToken(user);
        revokedAllUserTokens(user);
        saveUserToken(user,accessToken);
        return  ResponseEntity.ok(new TokenResponse(accessToken,refreshToken,user.getName(),""));

    }
    private void saveUserToken(Users user, String jwtToken)
    {
        Token token = new Token();
        token.setUser(user);
        token.setToken(jwtToken);
        token.setTokenType(Token.TokenType.BEARER);
        token.setRevoked(false);
        token.setExpired(false);
        tokenRepository.save(token);
    }
    private void revokedAllUserTokens(Users users)
    {
        List<Token> validUserTokens = tokenRepository.findAllValidIsFalseOrRevokedIsFalseByUserId(users.getId());
        if(!validUserTokens.isEmpty())
        {
            for(Token token:validUserTokens)
            {
                token.setExpired(true);
                token.setRevoked(true);
            }
            tokenRepository.saveAll(validUserTokens);
        }
    }
}
