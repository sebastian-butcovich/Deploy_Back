package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.FileRequest.Fecha;
import com.example.tryJwt.demo.FileRequest.ListTotalResponse;
import com.example.tryJwt.demo.FileRequest.TotalResponse;
import com.example.tryJwt.demo.Modelo.Flow;
import com.example.tryJwt.demo.Modelo.Income;
import com.example.tryJwt.demo.Modelo.Spent;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.IncomeRepository;
import com.example.tryJwt.demo.Repository.SpentRepository;
import com.example.tryJwt.demo.Utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private List<Flow> getListFlow(Map<String,String> params,Users users,String movemType) {
        List<Spent> spents = null;
        List<Income> incomes = null;
        List<Flow> flows = new ArrayList<>();
        if(movemType.equals("spent")) {
            if (params.containsKey("fecha_inicio") && params.containsKey("fecha_fin")) {
                spents=  spentRepository.findAllByUsuario(users.getId(), params.get("fecha_inicio"), params.get("fecha_fin"));
            } else {
                spents =  spentRepository.findAllByUsuario(users.getId());
            }
            flows.addAll(spents);
        }else if (movemType.equals("income")) {
            if (params.containsKey("fecha_inicio") && params.containsKey("fecha_fin")) {
                incomes = incomeRepository.findAllByUsuario(users.getId(), params.get("fecha_inicio"), params.get("fecha_fin"));
            }else
            {
                incomes = incomeRepository.findAllByUsuario(users.getId());
            }
            flows.addAll(incomes);
        }
        return flows;
    }
    public ResponseEntity<TotalResponse> getTotal(Map<String, String> params,String movemType ) {
        Users users = functionUtils.getUsers(params).orElseThrow();
        List<Flow> flow = getListFlow(params,users,movemType);
        if (flow.isEmpty()) {
            return ResponseEntity.ok().body(new TotalResponse(0.0, "", "No hay gastos agregados"));
        }
        double gasto = 0.0;
        if (params.get("currency") == null) {
            for (Flow i : flow) {
                gasto += i.getMonto();
            }
            return ResponseEntity.ok(new TotalResponse(gasto, "ars", "El total de lo ingresado"));
        }
        String current = params.get("currency");
        String current_type = params.get("currency_type");
        double value = functionUtils.getValue(current, current_type);
        functionUtils.changeCoins(flow, current, value);
        for (Flow i : flow) {
            gasto += i.getMonto();
        }
        double valorRedondeado = Math.round(gasto * 100.0) / 100.0;
        return ResponseEntity.ok(new TotalResponse(valorRedondeado, current, "El total de lo ingresado"));
    }

    public ResponseEntity<ListTotalResponse> getTotalGraphics(Map<String, String> params, List<Fecha> list, String movemType) {
        Users users = functionUtils.getUsers(params).orElseThrow();
        List<Double> respuesta = new ArrayList<Double>();
        double suma = 0.0;
        String fecha_inicio = list.get(0).fecha_string();
        String fecha_fin = list.get(list.size()-1).fecha_string();
        params.put("fecha_inicio", fecha_inicio);
        params.put("fecha_fin", fecha_fin);
        List<Flow> flows = getListFlow(params,users,movemType);
        flows = flows.reversed();
        int yearA = 0;
        int mesA =  0;
        int diaA =  0;
        int i = 0;
        if(i<=flows.size()-1)
        {
            diaA = flows.get(i).getFecha().getDate();
            mesA = flows.get(i).getFecha().getMonth()+1;
            yearA = flows.get(i).getFecha().getYear()+1900;
        }
        if(!params.containsKey("currency")){
            if(list.get(0).day() == 0 && list.get(0).month() == 0)
            {// Manipular años
                totalYears(list, i, flows, suma, respuesta,"not_current");
            }else if(list.get(0).day() == 0)
            {// Manipular meses
                generateTotalsMonth(list, i, flows, suma, respuesta,"not_current");
            }else if((list.get(0).day() + 7 == list.get(1).day() && list.get(0).month() == list.get(1).month())
            ||(list.get(0).month()+1 == list.get(1).month() && list.get(0).day() > list.get(1).day())){
                //Manipular semanas
                generatedTotalsWeek(list, i, flows, yearA, mesA, diaA, suma, respuesta,"not_current");
            }else{
                //manipular  dias
                generatedTotalsDays(list, i, flows, yearA, suma, diaA, mesA, respuesta,"not_current");
            }
            return  ResponseEntity.ok(new ListTotalResponse(respuesta,"ars","Los valores totales para los gráficos"));
        }else {
            String current = params.get("currency");
            String current_type = params.get("currency_type");
            double value = functionUtils.getValue(current,current_type);
            functionUtils.changeCoins(flows, current, value);
            if(list.get(0).day() == 0 && list.get(0).month() == 0)
            {// Manipular años
                totalYears(list, i, flows, suma, respuesta,current);
            }else if(list.get(0).day() == 0)
            {// Manipular meses
                generateTotalsMonth(list, i, flows, suma, respuesta,current);

            }else if((list.get(0).day() + 7 == list.get(1).day() && list.get(0).month() == list.get(1).month())
                    ||(list.get(0).month()+1 == list.get(1).month() && list.get(0).day() > list.get(1).day())){
                //Manipular semanas
                generatedTotalsWeek(list, i, flows, yearA, mesA, diaA, suma, respuesta,current);
            }else{
                //manipular dias
                generatedTotalsDays(list, i, flows, yearA, suma, diaA, mesA, respuesta,current);
            }
            return  ResponseEntity.ok(new ListTotalResponse(respuesta,current,"Los valores totales para los graficos"));
        }
    }

    private static void generatedTotalsDays(List<Fecha> list, int i, List<Flow> flows, int yearA, double suma, int diaA,
                                            int mesA, List<Double> respuesta,String current) {
        int diaF;
        int yearI;
        int yearF;
        int diaI;
        int mesF;
        int mesI;
        for(int j = 0; j< list.size()-1; j++)
        {
            yearI = list.get(j).year();
            yearF = list.get(j+1).year();
            mesI = list.get(j).month();
            mesF = list.get(j+1).month();
            diaI = list.get(j).day();
            diaF = list.get(j+1).day();
            //Toma el dia final de un año
            while( i <= flows.size()-1&&yearI != yearF && yearI<= yearA && yearA <yearF )
            {
                suma += flows.get(i).getMonto();
                i++;
                if(i <= flows.size()-1)
                {
                    diaA = flows.get(i).getFecha().getDate();
                    mesA = flows.get(i).getFecha().getMonth()+1;
                    yearA = flows.get(i).getFecha().getYear()+1900;
                }
            }
            while(i <= flows.size()-1&&yearI == yearF && mesI<= mesA
                    && mesA <mesF) {
                suma += flows.get(i).getMonto();
                i++;
                if (i <= flows.size() - 1) {
                    diaA = flows.get(i).getFecha().getDate();
                    mesA = flows.get(i).getFecha().getMonth() + 1;
                    yearA = flows.get(i).getFecha().getYear() + 1900;
                }
            }
            // Mismo mes y mismo año en una semana cualquiera
            while(i <= flows.size()-1 && yearI<= yearA &&  yearA <=yearF && mesI<= mesA && mesA <=mesF && diaI<= diaA
                    && diaA <diaF ){
                suma += flows.get(i).getMonto();
                i++;
                if(i <= flows.size()-1)
                {
                    diaA = flows.get(i).getFecha().getDate();
                    mesA = flows.get(i).getFecha().getMonth()+1;
                    yearA = flows.get(i).getFecha().getYear()+1900;
                }
            }
            if(current == null || current.equals("not_current")){
                respuesta.add(suma);
            }else
            {
                double valorRedondeado = Math.round(suma * 100.0) / 100.0;
                respuesta.add(valorRedondeado);
            }
            suma =0.0;
        }

    }

    private static void generatedTotalsWeek(List<Fecha> list, int i, List<Flow> flows, int yearA, int mesA, int diaA,
                                            double suma, List<Double> respuesta, String current) {
        int yearF;
        int diaI;
        int diaF;
        int yearI;
        int mesI;
        int mesF;
        for(int j = 0; j< list.size()-1; j++) {
            yearI = list.get(j).year();
            yearF = list.get(j + 1).year();
            mesI = list.get(j).month();
            mesF = list.get(j + 1).month();
            diaI = list.get(j).day();
            diaF = list.get(j + 1).day();
            //caso3: cambia de año
            while( i <= flows.size()-1&&yearI != yearF && yearI<= yearA &&
                    yearA + 1900<yearF && mesI>= mesA && mesF<= mesA && diaI> diaA && diaA <diaF )
            {
                suma += flows.get(i).getMonto();
                i++;
                if(i <= flows.size()-1)
                {
                    yearA = flows.get(i).getFecha().getYear() + 1900;
                    mesA = flows.get(i).getFecha().getMonth()+1;
                    diaA = flows.get(i).getFecha().getDate();
                }
            }
            //caso2.1: el año es el mismo, pero cambia el mes de una semana a la otra tomando
            // los días antes de finde de mes
            while(i <= flows.size()-1 && yearI == yearF && mesI<= mesA
                    && mesA <mesF && diaF< diaA)
            {
                suma += flows.get(i).getMonto();
                i++;
                if(i <= flows.size()-1)
                {
                    yearA = flows.get(i).getFecha().getYear() + 1900;
                    mesA = flows.get(i).getFecha().getMonth()+1;
                    diaA = flows.get(i).getFecha().getDate();
                }
            }
            //caso 2.2: mismo año pero cambia el mes tomando los dias de comienzo de mes
            while(i <= flows.size()-1 && yearI == yearF && mesI< mesA
                    && mesA <=mesF && diaA <diaF)
            {
                suma += flows.get(i).getMonto();
                i++;
                if(i <= flows.size()-1)
                {
                    yearA = flows.get(i).getFecha().getYear() + 1900;
                    mesA = flows.get(i).getFecha().getMonth()+1;
                    diaA = flows.get(i).getFecha().getDate();
                }
            }
            //caso 1: no cambia de mes ni de año y solo se toma en cuenta los dias
            while(i <= flows.size()-1 && yearI== yearF && mesI<= mesA && mesA <=mesF && diaI<= diaA
                    && diaA <diaF ){
                suma += flows.get(i).getMonto();
                i++;
                if(i <= flows.size()-1)
                {
                    yearA = flows.get(i).getFecha().getYear() + 1900;
                    mesA = flows.get(i).getFecha().getMonth()+1;
                    diaA = flows.get(i).getFecha().getDate();
                }
            }
            if(current == null || current.equals("not_current")){
                respuesta.add(suma);
                suma =0.0;
            }else
            {
                double valorRedondeado = Math.round(suma * 100.0) / 100.0;
                respuesta.add(valorRedondeado);
            }
            suma=0.0;
        }
    }

    private static void generateTotalsMonth(List<Fecha> list, int i, List<Flow> flows, double suma, List<Double> respuesta,String current) {
        int yearF;
        int yearI;
        int mesF;
        int mesI;
        for(int j = 0; j< list.size()-1; j++)
        {
            yearI = list.get(j).year();
            yearF = list.get(j+1).year();
            mesI = list.get(j).month();
            mesF = list.get(j+1).month();
            while(yearI != yearF && i <= flows.size()-1&&yearI<= flows.get(i).getFecha().getYear()+1900 &&
                flows.get(i).getFecha().getYear()+1900<yearF )
            {
                suma += flows.get(i).getMonto();
                i++;
            }
            while(i <= flows.size()-1&&yearI == yearF && mesI<= flows.get(i).getFecha().getMonth()+1
                    && flows.get(i).getFecha().getMonth()+1<mesF)
            {
                suma += flows.get(i).getMonto();
                i++;
            }
            if(current == null || current.equals("not_current")){
                respuesta.add(suma);
                suma =0.0;
            }else
            {
                double valorRedondeado = Math.round(suma * 100.0) / 100.0;
                respuesta.add(valorRedondeado);
            }
            suma=0.0;
        }
    }

    private static void totalYears(List<Fecha> list, int i, List<Flow> flows, double suma, List<Double> respuesta, String current) {
        int yearF;
        int yearI;
        for(int j = 0; j< list.size()-1; j++){
            yearI = list.get(j).year();
            yearF = list.get(j+1).year();
            while(i <= flows.size()-1&&yearI<= flows.get(i).getFecha().getYear()+1900 &&
                    flows.get(i).getFecha().getYear()+1900<yearF )
            {
                suma += flows.get(i).getMonto();
                i++;
            }
            if(current == null || current.equals("not_current")){
                respuesta.add(suma);
            }else
            {
                double valorRedondeado = Math.round(suma * 100.0) / 100.0;
                respuesta.add(valorRedondeado);
            }
            suma =0.0;
        }
    }

}
