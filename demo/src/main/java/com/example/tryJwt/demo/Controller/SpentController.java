package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.FileRequest.ParamsHeader;
import com.example.tryJwt.demo.FileRequest.SpentRequest;
import com.example.tryJwt.demo.Modelo.Spent;
import com.example.tryJwt.demo.Servicies.SpentService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<List<Spent>> listarGastos(@RequestParam Map<String,String> headers)
    {
        return spentService.listSpent(headers);
    }
    @GetMapping("/oneSpent")
    public ResponseEntity<Spent> obtenerGasto(@RequestHeader  Integer idSpent)
    {
        return spentService.obtenerGasto(idSpent);
    }
    @PostMapping("/add")
    public ResponseEntity<String> agregarGasto(@RequestBody  SpentRequest spent, @RequestParam Map<String,String> headers )
    {
        return spentService.addSpent(spent,headers);
    }
    @PutMapping("/update")
    public ResponseEntity<String> editarGasto(@RequestBody SpentRequest spent, @RequestParam Map<String,String>params)
    {
        return spentService.editSpent(spent,params);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> eliminarGasto(@RequestParam  Integer id)
    {

        return spentService.removeSpent(id);
    }
    @GetMapping("/tipos")
    public ResponseEntity<List<String>> obtenerTipos(@RequestParam Map<String,String> params)
    {
       return  spentService.obtenerTipos(params);
    }
}
