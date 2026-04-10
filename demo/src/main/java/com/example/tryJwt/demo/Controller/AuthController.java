package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.FileRequest.Request.ChangePasswordRequest;
import com.example.tryJwt.demo.FileRequest.Request.LoginRequest;
import com.example.tryJwt.demo.FileRequest.Responses.LoginResponse;
import com.example.tryJwt.demo.FileRequest.UsuarioDto;
import com.example.tryJwt.demo.FileRequest.Responses.TokenResponse;
import com.example.tryJwt.demo.Services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService service;


    @PostMapping("/register")
    public ResponseEntity<Object> register (@RequestBody final UsuarioDto request) {
        final TokenResponse token = service.register(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public final ResponseEntity<Object> authenticate(@RequestBody final LoginRequest request) {
        final LoginResponse token = service.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshAccessToken(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String accessToken,
                                                     @RequestBody String refreshToken) {
        try {
            return ResponseEntity.ok(service.refreshAccessToken(accessToken, refreshToken));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Object> validate(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        return ResponseEntity.ok(service.validate(token));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<Object> changePassword(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                                 @RequestBody ChangePasswordRequest req) {
        try {
            service.changePassword(token, req);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
