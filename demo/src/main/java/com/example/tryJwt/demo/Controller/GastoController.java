package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.Enums.TipoActualFlow;
import com.example.tryJwt.demo.FileRequest.Fecha;
import com.example.tryJwt.demo.FileRequest.ListTotalResponse;
import com.example.tryJwt.demo.FileRequest.MovementsRequest;
import com.example.tryJwt.demo.FileRequest.TotalResponse;
import com.example.tryJwt.demo.Modelo.ActualFlow;
import com.example.tryJwt.demo.Services.ActualFlowsService;
import com.example.tryJwt.demo.Services.DashboardService;
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
@RequestMapping("api/gasto")
@CrossOrigin(origins = "*")
public class GastoController {

    @Autowired
    private ActualFlowsService service;

    @Autowired
    private DashboardService dashboardService;

    private static final String urlBase = "api/gasto";

    private static final TipoActualFlow tipo = TipoActualFlow.GASTO;

    @GetMapping("/list")
    public ResponseEntity<Object> listaIngresos(@RequestParam Map<String, String> params,
                                                @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(service.pagedList(params, token, tipo));
        } catch(EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable int id,
                                      @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(service.get(id, token, tipo));
        } catch(EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody MovementsRequest af,
                                      @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token,
                                      UriComponentsBuilder uriBuilder) {
        try {
            ActualFlow dto = service.add(af, token, tipo);
            URI location = uriBuilder.path(urlBase + "/{id}").buildAndExpand(dto.getId()).toUri();
            return ResponseEntity.created(location).body(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable int id,
                                         @RequestBody MovementsRequest af,
                                         @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
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
                                         @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
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
    public ResponseEntity<Object> getAllSubtypes(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        try {
            return ResponseEntity.ok(service.getAllSubtypes(token, tipo));
        } catch(EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }





    @GetMapping("/total")
    @CrossOrigin(origins = "*")
    public ResponseEntity<TotalResponse> getTotal(@RequestParam Map<String,String> param)
    {
        return dashboardService.getTotal(param,"spent");
    }
    @PutMapping("/totalGraphics")
    @CrossOrigin(origins = "*")
    public ResponseEntity<ListTotalResponse> getTotalGraphics(@RequestParam Map<String,String> param, @RequestBody List<Fecha> list)
    {
        return dashboardService.getTotalGraphics(param,list,"spent");
    }
}
