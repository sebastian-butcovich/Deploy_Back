package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.Enums.TipoFutureFlow;
import com.example.tryJwt.demo.FileRequest.FutureFlowDto;
import com.example.tryJwt.demo.Modelo.FutureFlow;
import com.example.tryJwt.demo.Services.FutureFlowsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/deuda")
public class DeudaController {

    @Autowired
    private FutureFlowsService service;

    private static final String urlBase = "api/deuda";

    private static final TipoFutureFlow tipo = TipoFutureFlow.DEUDA;

    @GetMapping("/list")
    public ResponseEntity<Object> list(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                         @RequestParam Map<String,String> params) {
        return ResponseEntity.ok(service.list(token, params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable int id,
                                      @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(service.get(id, token, tipo));
        } catch(EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody FutureFlowDto ff,
                                          @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                          UriComponentsBuilder uriBuilder) {
        try {
            FutureFlow dto = service.add(ff, token, tipo);
            URI location = uriBuilder.path(urlBase + "/{id}").buildAndExpand(dto.getId()).toUri();
            return ResponseEntity.created(location).body(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable int id,
                                         @RequestBody FutureFlowDto ff,
                                         @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        try {
            FutureFlow dto = service.update(id, ff, token, tipo);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id,
                                           @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        try {
            service.delete(id, token, tipo);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
