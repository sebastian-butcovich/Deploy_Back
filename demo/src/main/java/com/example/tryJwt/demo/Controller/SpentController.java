package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.FileRequest.*;
import com.example.tryJwt.demo.Modelo.Spent;
import com.example.tryJwt.demo.Servicies.DashboardService;
import com.example.tryJwt.demo.Servicies.SpentService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("spent")
public class SpentController {
    @Autowired
    private SpentService spentService;
    @Autowired
    private DashboardService dashboardService;
    @GetMapping("/get_all")
    @JsonFormat
    public ResponseEntity<MovementsResponse> listarGastos(@RequestParam Map<String,String> headers)
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
    @GetMapping("/total")
    @CrossOrigin(origins = "*")
    public ResponseEntity<TotalResponse> getTotalGastos(@RequestParam Map<String, String> param)
    {
        return dashboardService.getTotalGastos(param);
    }
    @PutMapping("/totalGraphics")
    @CrossOrigin(origins = "*")
    public ResponseEntity<ListTotalResponse> getTotalSpentGraphics(@RequestParam Map<String,String> param, @RequestBody List<Fecha> list)
    {
        return dashboardService.getTotalGastosGraphics(param,list);
    }
}
