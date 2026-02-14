package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.Enums.TipoActualFlow;
import com.example.tryJwt.demo.FileRequest.*;
import com.example.tryJwt.demo.Modelo.ActualFlow;
import com.example.tryJwt.demo.Services.DashboardService;
import com.example.tryJwt.demo.Services.ActualFlowsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/ingreso")
@CrossOrigin(origins = "*")
public class IngresoController {

    @Autowired
    private ActualFlowsService service;

    @Autowired
    private DashboardService dashboardService;

    private static final String urlBase = "api/ingreso";

    private static final TipoActualFlow tipo = TipoActualFlow.INGRESO;

    @GetMapping("/list")
    public ResponseEntity<Object> list(@RequestParam Map<String, String> params,
                                                @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(service.pagedList(params, token, tipo));
        } catch(EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }

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
    public ResponseEntity<Object> add(@RequestBody MovementsRequest af,
                                      @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token,
                                      UriComponentsBuilder uriBuilder) {
        try {
            ActualFlow dto = service.add(af, token, tipo);
            URI location = uriBuilder.path(urlBase + "/{id}").buildAndExpand(dto.getId()).toUri();
            return ResponseEntity.created(location).body(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable int id,
                                         @RequestBody MovementsRequest af,
                                         @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        try {
            ActualFlow dto = service.update(id, af, token, tipo);
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

    @GetMapping("/subtipos")
    public ResponseEntity<Object> getAllSubtypes(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(service.getAllSubtypes(token, tipo));
        } catch(EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/total")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Object> getTotal(@RequestParam Map<String,String> params,
                                           @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token)
    {
        try {
            return ResponseEntity.ok(dashboardService.getTotal(params, token, tipo));
        } catch(EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/totalGraphics")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Object> getTotalGraphics(@RequestParam Map<String,String> params,
                                                   @RequestBody List<Fecha> list,
                                                   @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token)
    {
        try {
            return ResponseEntity.ok(dashboardService.getTotalGraphics(params, list, token, tipo));
        } catch(EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
