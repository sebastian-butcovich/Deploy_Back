package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.FileRequest.DebtsResponse;
import com.example.tryJwt.demo.Modelo.Debts;
import com.example.tryJwt.demo.Servicies.DebtsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class DebtsController {
    @Autowired
    private DebtsService debtsService;

    public ResponseEntity<DebtsResponse> listarDebts(@RequestParam Map<String, String> params)
    {
     return debtsService.listarDebts(params);
    }
    public ResponseEntity<String> agregarDebts(@RequestParam String token, @RequestBody Debts debts)
    {
        return debtsService.agregarDebts(token,debts);
    }
    public ResponseEntity<String> editarDebts(@RequestParam String token, @RequestBody Debts debts)
    {
        return debtsService.editarDebts(token,debts);
    }
    public ResponseEntity<String> eliminarDebts(@RequestParam String token, @RequestParam Integer id)
    {
        return debtsService.eliminarDebts(token,id);
    }
}
