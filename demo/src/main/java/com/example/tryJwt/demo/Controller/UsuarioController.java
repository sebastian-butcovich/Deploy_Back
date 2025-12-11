package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.FileRequest.UsuarioDto;
import com.example.tryJwt.demo.Services.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    UsuarioService service;

    private static final String urlBase = "/api/usuarios";

    @GetMapping
    public ResponseEntity<Object> whoAmI(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        try {
            UsuarioDto dto = service.whoAmI(token);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Object> list(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        try {
            Iterable<UsuarioDto> list = service.getAll(token);
            return ResponseEntity.ok(list);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (AuthorizationDeniedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<Object> updateMyself(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token,
                                               @RequestBody UsuarioDto usuarioDto) {
        try {
            UsuarioDto dto = service.updateMyself(token, usuarioDto);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteMyself(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        try {
            service.deleteMyself(token);
            return null;
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token,
                                      @PathVariable Long id) {
        try {
            UsuarioDto dto = service.get(token, id);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (AuthorizationDeniedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token,
                                         @PathVariable Long id,
                                         @RequestBody UsuarioDto usuarioDto) {
        try {
            UsuarioDto dto = service.update(token, id, usuarioDto);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (AuthorizationDeniedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token,
                                         @PathVariable Long id) {
        try {
            service.delete(token, id);
            return null;
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (AuthorizationDeniedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
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