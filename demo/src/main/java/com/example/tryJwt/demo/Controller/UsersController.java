package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.FileRequest.UpdateUsers;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Services.UsersService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/users")
@CrossOrigin(origins = "*")
public class UsersController {

    @Autowired
    private UsersService service;

    @GetMapping
    public ResponseEntity<Object> list(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(service.list(token));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @CrossOrigin(origins = "*")
    @PutMapping
    public ResponseEntity<Object> update(@RequestBody UpdateUsers updateUsers,
                                         @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        try {
            Users user = service.update(updateUsers, token);
            return ResponseEntity.ok(user);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Object> whoAmI(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                         @RequestParam Map<String,String> params) {
        try {
            return ResponseEntity.ok(service.whoAmI(token, params));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        try {
            service.delete(token);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }


    @PutMapping("/actualizarValorActual")
    public ResponseEntity<Object> actualizarValorActual(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                                        @RequestParam Double valorActual){
        try {
            return ResponseEntity.ok(service.actualizarValorActual(token,valorActual));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}