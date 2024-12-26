package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.Servicies.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@Controller
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private  AuthService service;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register (@RequestBody final RegisterRequest request)
    {
        final TokenResponse token = service.register(request);
        return ResponseEntity.ok(token);
    }
    @PostMapping("/login")
    public final ResponseEntity<TokenResponse> authenticate(@RequestBody final LoginRequest request)
    {
        final TokenResponse token = service.login(request);
        return ResponseEntity.ok(token);
    }
    @PostMapping("/refresh")
    public TokenResponse refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader )
    {
        return service.refreshToken(authHeader);
    }
    @GetMapping("/vital")
    public String vital()
    {
        return new String ("El servicio esta vivo");
    }
}
