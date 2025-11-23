package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.FileRequest.FutureFlowPagedResponse;
import com.example.tryJwt.demo.Modelo.FutureFlow;
import com.example.tryJwt.demo.Services.FutureFlowsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/flow")
public class FutureFlowsController {

    @Autowired
    private FutureFlowsService debtsService;

    private static final String urlBase = "api/flow";

    @GetMapping("/list")
    public ResponseEntity<FutureFlowPagedResponse> listarDebts(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token,
                                                               @RequestParam Map<String,String> params) {
     return debtsService.listarDebts(token, params);
    }

    @PostMapping("/add")
    public ResponseEntity<String> agregarDebts(@RequestBody FutureFlow debts,
                                               @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token,
                                               @RequestParam Map<String,String> params) {
        return debtsService.agregarDebts(debts, token, params);
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editarDebts(@RequestBody FutureFlow debts,
                                              @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token,
                                              @RequestParam Map<String,String> params) {
        return debtsService.editarDebts(debts, token, params);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> eliminarDebts(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) {
        return debtsService.eliminarDebts(token);
    }
}
