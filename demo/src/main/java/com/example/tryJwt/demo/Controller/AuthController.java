package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.FileRequest.LoginRequest;
import com.example.tryJwt.demo.FileRequest.RegisterRequest;
import com.example.tryJwt.demo.FileRequest.TokenResponse;
import com.example.tryJwt.demo.Services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private  AuthService service;

    @PostMapping("/register")
    public ResponseEntity<Object> register (@RequestBody final RegisterRequest request) {
        final TokenResponse token = service.register(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public final ResponseEntity<Object> authenticate(@RequestBody final LoginRequest request) {
        final TokenResponse token = service.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshToken(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = true) String token) {
        try {
            return ResponseEntity.ok(service.refreshToken(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Object> validate(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = true) String token) {
        return ResponseEntity.ok(service.validate(token));
    }
}
