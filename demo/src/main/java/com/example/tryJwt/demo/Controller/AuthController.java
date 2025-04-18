package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.FileRequest.LoginRequest;
import com.example.tryJwt.demo.FileRequest.RegisterRequest;
import com.example.tryJwt.demo.FileRequest.TokenResponse;
import com.example.tryJwt.demo.Servicies.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
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
    public ResponseEntity<TokenResponse> refreshToken(@RequestParam Map<String, String> params )
    {
        return service.refreshToken(params);
    }
    @GetMapping("/vital")
    public String vital()
    {
        return new String ("El servicio esta vivo");
    }
}
