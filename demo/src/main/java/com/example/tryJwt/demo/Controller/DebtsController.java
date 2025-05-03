package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.FileRequest.DebtsResponse;
import com.example.tryJwt.demo.Modelo.Debts;
import com.example.tryJwt.demo.Servicies.DebtsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("debts")
public class DebtsController {
    @Autowired
    private DebtsService debtsService;
    @GetMapping("/list")
    public ResponseEntity<DebtsResponse> listarDebts(@RequestParam Map<String, String> params)
    {
     return debtsService.listarDebts(params);
    }
    @PostMapping("/add")
    public ResponseEntity<String> agregarDebts(@RequestBody Debts debts, @RequestParam Map<String, String> params)
    {
        return debtsService.agregarDebts(debts, params);
    }
    @PutMapping("/edit")
    public ResponseEntity<String> editarDebts( @RequestBody Debts debts, @RequestParam Map<String, String> params)
    {
        return debtsService.editarDebts(debts,params);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> eliminarDebts(@RequestParam Map<String,String> params)
    {
        return debtsService.eliminarDebts(params);
    }
}
