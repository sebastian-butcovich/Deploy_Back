package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.FileRequest.TotalResponse;
import com.example.tryJwt.demo.Modelo.Income;
import com.example.tryJwt.demo.Modelo.Spent;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.IncomeRepository;
import com.example.tryJwt.demo.Repository.SpentRepository;
import com.example.tryJwt.demo.Utils.FunctionUtils;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    @Autowired
    private IncomeRepository incomeRepository;
    @Autowired
    private SpentRepository spentRepository;
    @Autowired
    private FunctionUtils functionUtils;

    public ResponseEntity<TotalResponse>  getTotalGastos(Map<String, String> param)
    {
        Users users = functionUtils.getUsers(param).orElseThrow();
        List<Spent> spents =  spentRepository.findAllByUsuario(users.getId());
        if(spents.isEmpty())
        {
            return ResponseEntity.badRequest().body(new TotalResponse(0.0,"No hay gastos agregados"));
        }
        double gasto = 0.0;
        for(Spent l: spents)
        {
            gasto+= l.getMonto();
        }
    return ResponseEntity.ok(new TotalResponse(gasto,"El total de lo gastado"));
    }
    public ResponseEntity<TotalResponse> getTotalIngresos(Map<String,String> params)
    {
        Users users = functionUtils.getUsers(params).orElseThrow();
        List<Income> incomes = incomeRepository.findAllByUsuario(users.getId());
        if(incomes.isEmpty())
        {
            return ResponseEntity.badRequest().body(new TotalResponse(0.0,"No hay ingresos agregados"));
        }
        double ingresos = 0.0;
        for(Income i:incomes)
        {
            ingresos+=i.getMonto();
        }
        return ResponseEntity.ok(new TotalResponse(ingresos,"El total de lo ingresado"));
    }
}
