package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.FileRequest.*;
import com.example.tryJwt.demo.Modelo.Income;
import com.example.tryJwt.demo.Servicies.DashboardService;
import com.example.tryJwt.demo.Servicies.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("income")
@CrossOrigin(origins = "*")
public class IncomeController {
    @Autowired
    private IncomeService incomeService;
    @Autowired
    private DashboardService dashboardService;
    @GetMapping("/get_all")
    public ResponseEntity<MovementsResponse> listaIngresos(@RequestParam Map<String, String> params)
    {
      return   incomeService.listarIngresos(params);
    }
    @GetMapping("/oneIncome")
    public ResponseEntity<Income> obtenerIngreso(@RequestParam Map<String,String> params)
    {
        return incomeService.obtenerUnIngreso(params);
    }
    @PostMapping("/add")
    public ResponseEntity<String> agregarIngreso(@RequestBody MovementsRequest ingreso, @RequestParam Map<String,String> params )
    {
        return incomeService.agregarIngreso(ingreso,params);
    }
    @PostMapping("/update")
    public ResponseEntity<String> editarIngreso(@RequestBody MovementsRequest ingreso, @RequestParam Map<String,String> params)
    {
        return incomeService.editarIngreso(ingreso,params);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> eliminarIngreso(@RequestParam Map<String,String> params)
    {
        return incomeService.eliminarIngreso(params);
    }
    @GetMapping("/tipos")
    public ResponseEntity<HashSet<String>> obtenerTiposIngresos(@RequestParam Map<String,String> params)
    {
    return incomeService.obtenerTiposIngreso(params);
    }
    @GetMapping("/total")
    @CrossOrigin(origins = "*")
    public ResponseEntity<TotalResponse> getTotalIngreso(@RequestParam Map<String,String> param)
    {
        return dashboardService.getTotal(param,"income");
    }
    @PutMapping("/totalGraphics")
    @CrossOrigin(origins = "*")
    public ResponseEntity<ListTotalResponse> getTotalIngresoGraphics(@RequestParam Map<String,String> param, @RequestBody List<Fecha> list)
    {
        return dashboardService.getTotalGraphics(param,list,"income");
    }
}
