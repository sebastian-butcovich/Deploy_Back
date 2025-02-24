package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.FileRequest.Fecha;
import com.example.tryJwt.demo.FileRequest.ListTotalResponse;
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

import java.util.ArrayList;
import java.util.Date;
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

    public ResponseEntity<TotalResponse>getTotalGastos(Map<String, String> params)
    {
        Users users = functionUtils.getUsers(params).orElseThrow();
        List<Spent> spents = null;
        if(params.containsKey("fecha_inicio") && params.containsKey("fecha_fin"))
        {
            spents = spentRepository.findAllByUsuario(users.getId(),params.get("fecha_inicio"),params.get("fecha_fin"));
        }else {
            spents = spentRepository.findAllByUsuario(users.getId());
        }
        if(spents.isEmpty())
        {
            return ResponseEntity.ok().body(new TotalResponse(0.0,"","No hay gastos agregados"));
        }
        double gasto = 0.0;
        if(params.get("currency") == null){
            for(Spent i:spents)
            {
                gasto+=i.getMonto();
            }
            return ResponseEntity.ok(new TotalResponse(gasto,"args","El total de lo ingresado"));
        }
        String current = params.get("currency");
        String current_type = params.get("currency_type");
        functionUtils.changeCoinsSpent(spents,current,current_type);
        for(Spent i:spents)
        {
            gasto+=i.getMonto();
        }
        double valorRedondeado = Math.round(gasto * 100.0) / 100.0;
        return ResponseEntity.ok(new TotalResponse(valorRedondeado,current,"El total de lo ingresado"));
    }
    public ResponseEntity<ListTotalResponse> getTotalGastosGraphics(Map<String,String>params, List<Fecha> list)
    {
        Users users = functionUtils.getUsers(params).orElseThrow();
        List<Double> respuesta = new ArrayList<Double>();
        double suma = 0.0;
        if(!params.containsKey("currency")){

            for(int j=0;j<list.size()-1;j++) {
                List<Spent> spents = spentRepository.findAllByUsuario(users.getId(), list.get(j).fecha_string(), list.get(j + 1).fecha_string());
                for (Spent i : spents) {
                    suma += i.getMonto();
                }
                respuesta.add(suma);
                suma=0.0;
            }
            return  ResponseEntity.ok(new ListTotalResponse(respuesta,"args","Los valores totales para los gráficos"));
        }else {
            String current = params.get("currency");
            String current_type = params.get("currency_type");
            for (int j = 0; j < list.size() - 1; j++) {
                List<Spent> spents = spentRepository.findAllByUsuario(users.getId(), list.get(j).fecha_string(), list.get(j + 1).fecha_string());
                functionUtils.changeCoinsSpent(spents, current, current_type);
                for (Spent i : spents) {
                    suma += i.getMonto();
                }
                double valorRedondeado = Math.round(suma * 100.0) / 100.0;
                respuesta.add(valorRedondeado);
            }
            return  ResponseEntity.ok(new ListTotalResponse(respuesta,current,"Los valores totales para los graficos"));
        }
    }
    public ResponseEntity<TotalResponse> getTotalIngresos(Map<String,String> params)
    {
        Users users = functionUtils.getUsers(params).orElseThrow();
        List<Income> incomes = null;
        if(params.containsKey("fecha_inicio") && params.containsKey("fecha_fin"))
        {
             incomes = incomeRepository.findAllByUsuario(users.getId(),params.get("fecha_inicio"),params.get("fecha_fin"));
        }else
        {
             incomes = incomeRepository.findAllByUsuario(users.getId());
        }
        if(incomes.isEmpty())
        {
            return ResponseEntity.ok().body(new TotalResponse(0.0,"","No hay ingresos agregados"));
        }
        double ingresos = 0.0;
        if(!params.containsKey("currency")){
            for(Income i:incomes)
            {
                ingresos+=i.getMonto();
            }
            return ResponseEntity.ok(new TotalResponse(ingresos,"args","El total de lo ingresado"));
        }
        String current = params.get("currency");
        String current_type = params.get("currency_type");
        functionUtils.changeCoinsIncome(incomes,current,current_type);
        for(Income i:incomes)
        {
            ingresos+=i.getMonto();
        }
        double valorRedondeado = Math.round(ingresos * 100.0) / 100.0;
        return ResponseEntity.ok(new TotalResponse(valorRedondeado,current,"El total de lo ingresado"));
    }
    public ResponseEntity<ListTotalResponse> getTotalIngresosGraphics(Map<String,String>params, List<Fecha> list)
    {
        Users users = functionUtils.getUsers(params).orElseThrow();
        List<Double> respuesta = new ArrayList<Double>();
        double suma = 0.0;
        if(!params.containsKey("currency")){

            for(int j=0;j<list.size()-1;j++) {
                List<Income> incomes = incomeRepository.findAllByUsuario(users.getId(), list.get(j).fecha_string(), list.get(j + 1).fecha_string());
                for (Income i : incomes) {
                    suma += i.getMonto();
                }
                respuesta.add(suma);
                suma=0.0;
            }
            return  ResponseEntity.ok(new ListTotalResponse(respuesta,"args","Los valores totales para los gráficos"));
        }else {
            String current = params.get("currency");
            String current_type = params.get("currency_type");
            for (int j = 0; j < list.size() - 1; j++) {
                List<Income> incomes = incomeRepository.findAllByUsuario(users.getId(), list.get(j).fecha_string(), list.get(j + 1).fecha_string());
                functionUtils.changeCoinsIncome(incomes, current, current_type);
                for (Income i : incomes) {
                    suma += i.getMonto();
                }
                double valorRedondeado = Math.round(suma * 100.0) / 100.0;
                respuesta.add(valorRedondeado);
            }
            return  ResponseEntity.ok(new ListTotalResponse(respuesta,current,"Los valores totales para los graficos"));
        }
    }
}
