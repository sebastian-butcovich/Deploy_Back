package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.FileRequest.Request.ChangePasswordRequest;
import com.example.tryJwt.demo.FileRequest.Request.LoginRequest;
import com.example.tryJwt.demo.FileRequest.Responses.LoginResponse;
import com.example.tryJwt.demo.FileRequest.UsuarioDto;
import com.example.tryJwt.demo.FileRequest.Responses.TokenResponse;
import com.example.tryJwt.demo.Services.AuthService;
import com.example.tryJwt.demo.Services.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse register (@RequestBody final UsuarioDto request) {
        try {
            return service.register(request);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public final LoginResponse authenticate(@RequestBody final LoginRequest request) {
        try {
            return service.login(request);
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse refreshAccessToken(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String accessToken,
                                                     @RequestHeader("refreshToken") String refreshToken) {
        try {
            return service.refreshAccessToken(accessToken, refreshToken);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Boolean> validate(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        try {
            return Map.of("token_expired", jwtService.isTokenExpired(token));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/changePassword")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                                 @RequestBody ChangePasswordRequest req) {
        try {
            service.changePassword(token, req);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
