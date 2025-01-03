package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.FileRequest.MovementsRequest;
import com.example.tryJwt.demo.Modelo.Spent;
import com.example.tryJwt.demo.Servicies.SpentService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;

@RestController
@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("spent")
public class SpentController {
    @Autowired
    private SpentService spentService;

    @GetMapping("/get_all")
    @JsonFormat
    public ResponseEntity<Page<Spent>> listarGastos(@RequestParam Map<String,String> headers)
    {
        return spentService.listSpent(headers);
    }
    @GetMapping("/oneSpent")
    public ResponseEntity<Spent> obtenerGasto(@RequestHeader  Integer idSpent)
    {
        return spentService.obtenerGasto(idSpent);
    }
    @PostMapping("/add")
    public ResponseEntity<String> agregarGasto(@RequestBody MovementsRequest spent, @RequestParam Map<String,String> headers )
    {
        return spentService.addSpent(spent,headers);
    }
    @PostMapping("/update")
    public ResponseEntity<String> editarGasto(@RequestBody MovementsRequest spent, @RequestParam Map<String,String>params)
    {
        return spentService.editSpent(spent,params);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> eliminarGasto(@RequestParam  Integer id)
    {

        return spentService.removeSpent(id);
    }
    @GetMapping("/tipos")
    public ResponseEntity<HashSet<String>> obtenerTipos(@RequestParam Map<String,String> params)
    {
       return  spentService.obtenerTipos(params);
    }
}
