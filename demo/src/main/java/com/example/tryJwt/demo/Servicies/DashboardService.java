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

    public ResponseEntity<TotalResponse> getTotalGastos(Map<String, String> params) {
        Users users = functionUtils.getUsers(params).orElseThrow();
        List<Spent> spents = null;
        if (params.containsKey("fecha_inicio") && params.containsKey("fecha_fin")) {
            spents = spentRepository.findAllByUsuario(users.getId(), params.get("fecha_inicio"), params.get("fecha_fin"));
        } else {
            spents = spentRepository.findAllByUsuario(users.getId());
        }
        if (spents.isEmpty()) {
            return ResponseEntity.ok().body(new TotalResponse(0.0, "", "No hay gastos agregados"));
        }
        double gasto = 0.0;
        if (params.get("currency") == null) {
            for (Spent i : spents) {
                gasto += i.getMonto();
            }
            return ResponseEntity.ok(new TotalResponse(gasto, "args", "El total de lo ingresado"));
        }
        String current = params.get("currency");
        String current_type = params.get("currency_type");
        double value = functionUtils.getValue(current, current_type);
        functionUtils.changeCoinsSpent(spents, current, value);
        for (Spent i : spents) {
            gasto += i.getMonto();
        }
        double valorRedondeado = Math.round(gasto * 100.0) / 100.0;
        return ResponseEntity.ok(new TotalResponse(valorRedondeado, current, "El total de lo ingresado"));
    }

    public ResponseEntity<ListTotalResponse> getTotalGastosGraphics(Map<String, String> params, List<Fecha> list) {
        Users users = functionUtils.getUsers(params).orElseThrow();
        List<Double> respuesta = new ArrayList<Double>();
        double suma = 0.0;
        String fecha_inicio = list.get(0).fecha_string();
        String fecha_fin = list.get(list.size()-1).fecha_string();
        List<Spent> spents = spentRepository.findAllByUsuarioFecha(users.getId(), fecha_inicio,fecha_fin);
        int yearI = 0;
        int yearF = 0;
        int mesI =  0;
        int mesF =  0;
        int diaI =  0;
        int diaF =  0;
        int yearA = 0;
        int mesA =  0;
        int diaA =  0;
        int i = 0;
        if(i<=spents.size()-1)
        {
            diaA = spents.get(i).getFecha().getDate();
            mesA = spents.get(i).getFecha().getMonth()+1;
            yearA = spents.get(i).getFecha().getYear()+1900;
        }
        if(!params.containsKey("currency")){
            if(list.get(0).day() == 0 && list.get(0).month() == 0)
            {// Manipular años
                for(int j =0; j<list.size()-1; j++){
                    yearI = list.get(j).year();
                    yearF = list.get(j+1).year();
                    while(i<=spents.size()-1&&yearI<=spents.get(i).getFecha().getYear()+1900 &&
                            spents.get(i).getFecha().getYear()+1900<yearF )
                    {
                        suma+=spents.get(i).getMonto();
                        i++;
                    }
                    respuesta.add(suma);
                    suma=0.0;
                }
            }else if(list.get(0).day() == 0)
            {// Manipular meses
                for(int j=0;j<list.size()-1;j++)
                {
                    yearI = list.get(j).year();
                    yearF = list.get(j+1).year();
                    mesI = list.get(j).month();
                    mesF = list.get(j+1).month();
                    while(yearI != yearF && i<=spents.size()-1&&yearI<=spents.get(i).getFecha().getYear()+1900 &&
                        spents.get(i).getFecha().getYear()+1900<yearF )
                    {
                        suma+= spents.get(i).getMonto();
                        i++;
                    }
                    while(i<=spents.size()-1&&yearI == yearF && mesI<=spents.get(i).getFecha().getMonth()+1
                            && spents.get(i).getFecha().getMonth()+1<mesF)
                    {
                        suma+=spents.get(i).getMonto();
                        i++;
                    }
                    respuesta.add(suma);
                    suma=0.0;
                }
            }else if((list.get(0).day() + 7 == list.get(1).day() && list.get(0).month() == list.get(1).month())
            ||(list.get(0).month()+1 == list.get(1).month() && list.get(0).day() > list.get(1).day())){
                //Manipular semanas
                for(int j=0;j<list.size()-1;j++) {
                    yearI = list.get(j).year();
                    yearF = list.get(j + 1).year();
                    mesI = list.get(j).month();
                    mesF = list.get(j + 1).month();
                    diaI = list.get(j).day();
                    diaF = list.get(j + 1).day();
                    //caso3: cambia de año
                    while( i<= spents.size()-1&&yearI != yearF && yearI<= yearA &&
                            yearA + 1900<yearF && mesI>=mesA && mesF<=mesA && diaI>diaA && diaA<diaF )
                    {
                        suma+= spents.get(i).getMonto();
                        i++;
                        if(i<=spents.size()-1)
                        {
                            yearA = spents.get(i).getFecha().getYear() + 1900;
                            mesA = spents.get(i).getFecha().getMonth()+1;
                            diaA = spents.get(i).getFecha().getDate();
                        }
                    }
                    //caso2.1: el año es el mismo, pero cambia el mes de una semana a la otra tomando
                    // los días antes de finde demes
                    while(i<spents.size()-1 && yearI == yearF && mesI<= mesA
                            && mesA<mesF && diaF<diaA)
                    {
                        suma+= spents.get(i).getMonto();
                        i++;
                        if(i<=spents.size()-1)
                        {
                            yearA = spents.get(i).getFecha().getYear() + 1900;
                            mesA = spents.get(i).getFecha().getMonth()+1;
                            diaA = spents.get(i).getFecha().getDate();
                        }
                    }
                    //caso 2.2: mismo año pero cambia el mes tomando los dias de comienzo de mes
                    while(i<=spents.size()-1 && yearI == yearF && mesI< mesA
                            && mesA<=mesF && diaA<diaF)
                    {
                        suma+= spents.get(i).getMonto();
                        i++;
                        if(i<=spents.size()-1)
                        {
                            yearA = spents.get(i).getFecha().getYear() + 1900;
                            mesA = spents.get(i).getFecha().getMonth()+1;
                            diaA = spents.get(i).getFecha().getDate();
                        }
                    }
                    //caso 1: no cambia de mes ni de año y solo se toma en cuenta los dias
                    while(i<= spents.size()-1 && yearI== yearF && mesI<=mesA && mesA<=mesF && diaI<= diaA
                            && diaA<diaF ){
                        suma+= spents.get(i).getMonto();
                        i++;
                        if(i<=spents.size()-1)
                        {
                            yearA = spents.get(i).getFecha().getYear() + 1900;
                            mesA = spents.get(i).getFecha().getMonth()+1;
                            diaA = spents.get(i).getFecha().getDate();
                        }
                    }
                    respuesta.add(suma);
                    suma=0;
                }
                }else{
                //manipular  dias
                for(int j=0;j<list.size()-1;j++)
                {
                    yearI = list.get(j).year();
                    yearF = list.get(j+1).year();
                    mesI = list.get(j).month();
                    mesF = list.get(j+1).month();
                    diaI = list.get(j).day();
                    diaF = list.get(j+1).day();
                    //Toma el dia final de un año
                    while( i<= spents.size()-1&&yearI != yearF && yearI<= yearA && yearA<yearF )
                    {
                        suma+= spents.get(i).getMonto();
                        i++;
                        if(i<=spents.size()-1)
                        {
                            diaA = spents.get(i).getFecha().getDate();
                            mesA = spents.get(i).getFecha().getMonth()+1;
                            yearA = spents.get(i).getFecha().getYear()+1900;
                        }
                    }
                    while(i<= spents.size()-1&&yearI == yearF && mesI<= mesA
                            && mesA<mesF) {
                        suma += spents.get(i).getMonto();
                        i++;
                        if (i <= spents.size() - 1) {
                            diaA = spents.get(i).getFecha().getDate();
                            mesA = spents.get(i).getFecha().getMonth() + 1;
                            yearA = spents.get(i).getFecha().getYear() + 1900;
                        }
                    }
                    // Mismo mes y mismo año en una semana cualquiera
                    while(i<= spents.size()-1 && yearI<= yearA &&  yearA<=yearF && mesI<=mesA && mesA<=mesF && diaI<=diaA
                            && diaA<diaF ){
                        suma+= spents.get(i).getMonto();
                        i++;
                        if(i<=spents.size()-1)
                        {
                            diaA = spents.get(i).getFecha().getDate();
                            mesA = spents.get(i).getFecha().getMonth()+1;
                            yearA = spents.get(i).getFecha().getYear()+1900;
                        }
                    }
                    respuesta.add(suma);
                    suma=0.0;
                }
            }
            return  ResponseEntity.ok(new ListTotalResponse(respuesta,"args","Los valores totales para los gráficos"));
        }else {
            String current = params.get("currency");
            String current_type = params.get("currency_type");
            double value = functionUtils.getValue(current,current_type);
            functionUtils.changeCoinsSpent(spents, current, value);
            if(list.get(0).day() == 0 && list.get(0).month() == 0)
            {// Manipular años
                for(int j =0; j<list.size()-1; j++){
                    yearI = list.get(j).year();
                    yearF = list.get(j+1).year();
                    while(i<=spents.size()-1&&yearI<=spents.get(i).getFecha().getYear()+1900 &&
                            spents.get(i).getFecha().getYear()+1900<yearF )
                    {
                        suma+=spents.get(i).getMonto();
                        i++;
                    }
                    double valorRedondeado = Math.round(suma * 100.0) / 100.0;
                    respuesta.add(valorRedondeado);
                    suma=0.0;
                }
            }else if(list.get(0).day() == 0)
            {// Manipular meses
                for(int j=0;j<list.size()-1;j++)
                {
                    yearI = list.get(j).year();
                    yearF = list.get(j+1).year();
                    mesI = list.get(j).month();
                    mesF = list.get(j+1).month();
                    while(yearI != yearF && i<=spents.size()-1&&yearI<=spents.get(i).getFecha().getYear()+1900 &&
                            spents.get(i).getFecha().getYear()+1900<yearF )
                    {
                        suma+= spents.get(i).getMonto();
                        i++;
                    }
                    while(i<=spents.size()-1&&yearI == yearF && mesI<=spents.get(i).getFecha().getMonth()+1
                            && spents.get(i).getFecha().getMonth()+1<mesF)
                    {
                        suma+=spents.get(i).getMonto();
                        i++;
                    }
                    double valorRedondeado = Math.round(suma * 100.0) / 100.0;
                    respuesta.add(valorRedondeado);
                    suma=0.0;
                }
            }else if((list.get(0).day() + 7 == list.get(1).day() && list.get(0).month() == list.get(1).month())
                    ||(list.get(0).month()+1 == list.get(1).month() && list.get(0).day() > list.get(1).day())){
                //Manipular semanas
                for(int j=0;j<list.size()-1;j++) {
                    yearI = list.get(j).year();
                    yearF = list.get(j + 1).year();
                    mesI = list.get(j).month();
                    mesF = list.get(j + 1).month();
                    diaI = list.get(j).day();
                    diaF = list.get(j + 1).day();
                    //caso3: cambia de año
                    while( i<= spents.size()-1&&yearI != yearF && yearI<= yearA &&
                            yearA + 1900<yearF && mesI>=mesA && mesF<=mesA && diaI>diaA && diaA<diaF )
                    {
                        suma+= spents.get(i).getMonto();
                        i++;
                        if(i<=spents.size()-1)
                        {
                            yearA = spents.get(i).getFecha().getYear() + 1900;
                            mesA = spents.get(i).getFecha().getMonth()+1;
                            diaA = spents.get(i).getFecha().getDate();
                        }
                    }
                    //caso2.1: el año es el mismo, pero cambia el mes de una semana a la otra tomando
                    // los días antes de finde demes
                    while(i<=spents.size()-1 && yearI == yearF && mesI<= mesA
                            && mesA<mesF && diaF<diaA)
                    {
                        suma+= spents.get(i).getMonto();
                        i++;
                        if(i<=spents.size()-1)
                        {
                            yearA = spents.get(i).getFecha().getYear() + 1900;
                            mesA = spents.get(i).getFecha().getMonth()+1;
                            diaA = spents.get(i).getFecha().getDate();
                        }
                    }
                    //caso 2.2: mismo año pero cambia el mes tomando los dias de comienzo de mes
                    while(i<=spents.size()-1 && yearI == yearF && mesI< mesA
                            && mesA<=mesF && diaA<diaF)
                    {
                        suma+= spents.get(i).getMonto();
                        i++;
                        if(i<=spents.size()-1)
                        {
                            yearA = spents.get(i).getFecha().getYear() + 1900;
                            mesA = spents.get(i).getFecha().getMonth()+1;
                            diaA = spents.get(i).getFecha().getDate();
                        }
                    }
                    //caso 1: no cambia de mes ni de año y solo se toma en cuenta los dias
                    while(i<= spents.size()-1 && yearI== yearF && mesI==mesF && diaI<= diaA
                            && diaA<diaF ){
                        suma+= spents.get(i).getMonto();
                        i++;
                        if(i<=spents.size()-1)
                        {
                            yearA = spents.get(i).getFecha().getYear() + 1900;
                            mesA = spents.get(i).getFecha().getMonth()+1;
                            diaA = spents.get(i).getFecha().getDate();
                        }
                    }
                    double valorRedondeado = Math.round(suma * 100.0) / 100.0;
                    respuesta.add(valorRedondeado);
                    suma=0;
                }
            }else{
                //manipular dias
                for(int j=0;j<list.size()-1;j++)
                {
                    yearI = list.get(j).year();
                    yearF = list.get(j+1).year();
                    mesI = list.get(j).month();
                    mesF = list.get(j+1).month();
                    diaI = list.get(j).day();
                    diaF = list.get(j+1).day();
                    while( i<=spents.size()-1&&yearI != yearF && yearI<=yearA &&
                            yearA<yearF )
                    {
                        suma+= spents.get(i).getMonto();
                        i++;
                        if(i<=spents.size()-1)
                        {
                            diaA = spents.get(i).getFecha().getDate();
                            mesA = spents.get(i).getFecha().getMonth()+1;
                            yearA = spents.get(i).getFecha().getYear()+1900;
                        }
                    }
                    while(i<=spents.size()-1&&yearI == yearF && mesI<=mesA
                            && mesA<mesF)
                    {
                        suma+=spents.get(i).getMonto();
                        i++;
                        if(i<=spents.size()-1)
                        {
                            diaA = spents.get(i).getFecha().getDate();
                            mesA = spents.get(i).getFecha().getMonth()+1;
                            yearA = spents.get(i).getFecha().getYear()+1900;
                        }
                    }

                    while(i<=spents.size()-1 && yearI== yearF && mesI<=mesA && mesA<=mesF && diaI<=diaA
                            && diaA<diaF ){
                        suma+=spents.get(i).getMonto();
                        i++;
                        if(i<=spents.size()-1)
                        {
                            diaA = spents.get(i).getFecha().getDate();
                            mesA = spents.get(i).getFecha().getMonth()+1;
                            yearA = spents.get(i).getFecha().getYear()+1900;
                        }
                    }
                    double valorRedondeado = Math.round(suma * 100.0) / 100.0;
                    respuesta.add(valorRedondeado);
                    suma=0.0;
                }
            }
            return  ResponseEntity.ok(new ListTotalResponse(respuesta,current,"Los valores totales para los graficos"));
        }
    }

    public ResponseEntity<TotalResponse> getTotalIngresos(Map<String, String> params) {
        Users users = functionUtils.getUsers(params).orElseThrow();
        List<Income> incomes = null;
        if (params.containsKey("fecha_inicio") && params.containsKey("fecha_fin")) {
            incomes = incomeRepository.findAllByUsuario(users.getId(), params.get("fecha_inicio"), params.get("fecha_fin"));
        } else {
            incomes = incomeRepository.findAllByUsuario(users.getId());
        }
        if (incomes.isEmpty()) {
            return ResponseEntity.ok().body(new TotalResponse(0.0, "", "No hay ingresos agregados"));
        }
        double ingresos = 0.0;
        if (!params.containsKey("currency")) {
            for (Income i : incomes) {
                ingresos += i.getMonto();
            }
            return ResponseEntity.ok(new TotalResponse(ingresos, "args", "El total de lo ingresado"));
        }
        String current = params.get("currency");
        String current_type = params.get("currency_type");
        double value = functionUtils.getValue(current, current_type);
        functionUtils.changeCoinsIncome(incomes, current, value);
        for (Income i : incomes) {
            ingresos += i.getMonto();
        }
        double valorRedondeado = Math.round(ingresos * 100.0) / 100.0;
        return ResponseEntity.ok(new TotalResponse(valorRedondeado, current, "El total de lo ingresado"));
    }

    public ResponseEntity<ListTotalResponse> getTotalIngresosGraphics(Map<String, String> params, List<Fecha> list) {
        Users users = functionUtils.getUsers(params).orElseThrow();
        List<Double> respuesta = new ArrayList<Double>();
        double suma = 0.0;
        String fecha_inicio = list.get(0).fecha_string();
        String fecha_fin = list.get(list.size()-1).fecha_string();
        List<Income> incomes = incomeRepository.findAllByUsuarioFecha(users.getId(), fecha_inicio,fecha_fin);
        int yearI = 0;
        int yearF = 0;
        int mesI =  0;
        int mesF =  0;
        int diaI =  0;
        int diaF =  0;
        int yearA = 0;
        int mesA = 0;
        int diaA = 0;
        int i = 0;
        if(i<=incomes.size()-1)
        {
            diaA = incomes.get(i).getFecha().getDate();
            mesA = incomes.get(i).getFecha().getMonth()+1;
            yearA = incomes.get(i).getFecha().getYear()+1900;
        }
        if(!params.containsKey("currency")){
            if(list.get(0).day() == 0 && list.get(0).month() == 0)
            {// Manipular años
                for(int j =0; j<list.size()-1; j++){
                    yearI = list.get(j).year();
                    yearF = list.get(j+1).year();
                    while(i<=incomes.size()-1&&yearI<= incomes.get(i).getFecha().getYear()+1900 &&
                            incomes.get(i).getFecha().getYear()+1900<yearF )
                    {
                        suma+=incomes.get(i).getMonto();
                        i++;
                    }
                    respuesta.add(suma);
                    suma=0.0;
                }
            }else if(list.get(0).day() == 0)
            {// Manipular meses
                for(int j=0;j<list.size()-1;j++)
                {
                    yearI = list.get(j).year();
                    yearF = list.get(j+1).year();
                    mesI = list.get(j).month();
                    mesF = list.get(j+1).month();
                    while(yearI != yearF && i<= incomes.size()-1&&yearI<= incomes.get(i).getFecha().getYear()+1900 &&
                            incomes.get(i).getFecha().getYear()+1900<yearF )
                    {
                        suma+= incomes.get(i).getMonto();
                        i++;
                    }
                    while(i<= incomes.size()-1&&yearI == yearF && mesI<= incomes.get(i).getFecha().getMonth()+1
                            && incomes.get(i).getFecha().getMonth()+1<mesF)
                    {
                        suma+= incomes.get(i).getMonto();
                        i++;
                    }
                    respuesta.add(suma);
                    suma=0.0;
                }
            }else  if((list.get(0).day() + 7 == list.get(1).day() && list.get(0).month() == list.get(1).month())
                    ||(list.get(0).month()+1 == list.get(1).month() && list.get(0).day() > list.get(1).day())){
                //Manipular semanas
                for(int j=0;j<list.size()-1;j++) {
                    yearI = list.get(j).year();
                    yearF = list.get(j + 1).year();
                    mesI = list.get(j).month();
                    mesF = list.get(j + 1).month();
                    diaI = list.get(j).day();
                    diaF = list.get(j + 1).day();
                    //caso3: cambia de año
                    while( i<= incomes.size()-1&&yearI != yearF && yearI<= yearA &&
                            yearA + 1900<yearF && mesI>=mesA && mesF<=mesA && diaI>diaA && diaA<diaF )
                    {
                        suma+= incomes.get(i).getMonto();
                        i++;
                        if(i<=incomes.size()-1)
                        {
                            yearA = incomes.get(i).getFecha().getYear() + 1900;
                            mesA = incomes.get(i).getFecha().getMonth()+1;
                            diaA = incomes.get(i).getFecha().getDate();
                        }
                    }
                    //caso2.1: el año es el mismo, pero cambia el mes de una semana a la otra tomando
                    // los días antes de finde de mes
                    while(i<=incomes.size()-1 && yearI == yearF && mesI<= mesA
                            && mesA<mesF && diaF<diaA)
                    {
                        suma+= incomes.get(i).getMonto();
                        i++;
                        if(i<=incomes.size()-1)
                        {
                            yearA = incomes.get(i).getFecha().getYear() + 1900;
                            mesA = incomes.get(i).getFecha().getMonth()+1;
                            diaA = incomes.get(i).getFecha().getDate();
                        }
                    }
                    //caso 2.2: mismo año pero cambia el mes tomando los dias de comienzo de mes
                    while(i<=incomes.size()-1 && yearI == yearF && mesI< mesA
                            && mesA<=mesF && diaA<diaF)
                    {
                        suma+= incomes.get(i).getMonto();
                        i++;
                        if(i<=incomes.size()-1)
                        {
                            yearA = incomes.get(i).getFecha().getYear() + 1900;
                            mesA = incomes.get(i).getFecha().getMonth()+1;
                            diaA = incomes.get(i).getFecha().getDate();
                        }
                    }
                    //caso 1: no cambia de mes ni de año y solo se toma en cuenta los dias
                    while(i<= incomes.size()-1 && yearI== yearF && mesI<= mesA && mesA<=mesF && diaI<= diaA
                            && diaA<diaF ){
                        suma+= incomes.get(i).getMonto();
                        i++;
                        if(i<=incomes.size()-1)
                        {
                            yearA = incomes.get(i).getFecha().getYear() + 1900;
                            mesA = incomes.get(i).getFecha().getMonth()+1;
                            diaA = incomes.get(i).getFecha().getDate();
                        }
                    }
                    respuesta.add(suma);
                    suma=0;
                }
            }else{
                //manipular  dias
                for(int j=0;j<list.size()-1;j++)
                {
                    yearI = list.get(j).year();
                    yearF = list.get(j+1).year();
                    mesI = list.get(j).month();
                    mesF = list.get(j+1).month();
                    diaI = list.get(j).day();
                    diaF = list.get(j+1).day();
                    while( i<= incomes.size()-1&&yearI != yearF && yearI<= yearA  && yearA<yearF )
                    {
                        suma+= incomes.get(i).getMonto();
                        i++;
                        if(i<=incomes.size()-1)
                        {
                            diaA = incomes.get(i).getFecha().getDate();
                            mesA = incomes.get(i).getFecha().getMonth()+1;
                            yearA = incomes.get(i).getFecha().getYear()+1900;
                        }
                    }
                    while(i<= incomes.size()-1&&yearI == yearF && mesI<= mesA && mesA<mesF)
                    {
                        suma+= incomes.get(i).getMonto();
                        i++;
                        if(i<=incomes.size()-1)
                        {
                            diaA = incomes.get(i).getFecha().getDate();
                            mesA = incomes.get(i).getFecha().getMonth()+1;
                            yearA = incomes.get(i).getFecha().getYear()+1900;
                        }
                    }
                    while(i<= incomes.size()-1 && yearI== yearF && mesI<=mesA && mesA<=mesF && diaI<= diaA && diaA<diaF ){
                        suma+= incomes.get(i).getMonto();
                        i++;
                        if(i<=incomes.size()-1)
                        {
                            diaA = incomes.get(i).getFecha().getDate();
                            mesA = incomes.get(i).getFecha().getMonth()+1;
                            yearA = incomes.get(i).getFecha().getYear()+1900;
                        }
                    }
                    respuesta.add(suma);
                    suma=0.0;
                }
            }
            return  ResponseEntity.ok(new ListTotalResponse(respuesta,"args","Los valores totales para los gráficos"));
        }else {
            String current = params.get("currency");
            String current_type = params.get("currency_type");
            double value = functionUtils.getValue(current,current_type);
            functionUtils.changeCoinsIncome(incomes, current, value);
            if(list.get(0).day() == 0 && list.get(0).month() == 0)
            {// Manipular años
                for(int j =0; j<list.size()-1; j++){
                    yearI = list.get(j).year();
                    yearF = list.get(j+1).year();
                    while(i<=incomes.size()-1&&yearI<=incomes.get(i).getFecha().getYear()+1900 &&
                            incomes.get(i).getFecha().getYear()+1900<yearF )
                    {
                        suma+=incomes.get(i).getMonto();
                        i++;
                    }
                    double valorRedondeado = Math.round(suma * 100.0) / 100.0;
                    respuesta.add(valorRedondeado);
                    suma=0.0;
                }
            }else if(list.get(0).day() == 0)
            {// Manipular meses
                for(int j=0;j<list.size()-1;j++)
                {
                    yearI = list.get(j).year();
                    yearF = list.get(j+1).year();
                    mesI = list.get(j).month();
                    mesF = list.get(j+1).month();
                    while(yearI != yearF && i<=incomes.size()-1&&yearI<=incomes.get(i).getFecha().getYear()+1900 &&
                            incomes.get(i).getFecha().getYear()+1900<yearF )
                    {
                        suma+= incomes.get(i).getMonto();
                        i++;
                    }
                    while(i<=incomes.size()-1&&yearI == yearF && mesI<=incomes.get(i).getFecha().getMonth()+1
                            && incomes.get(i).getFecha().getMonth()+1<mesF)
                    {
                        suma+=incomes.get(i).getMonto();
                        i++;
                    }
                    double valorRedondeado = Math.round(suma * 100.0) / 100.0;
                    respuesta.add(valorRedondeado);
                    suma=0.0;
                }
            }else if((list.get(0).day() + 7 == list.get(1).day() && list.get(0).month() == list.get(1).month())
                    ||(list.get(0).month()+1 == list.get(1).month() && list.get(0).day() > list.get(1).day())){
                //Manipular semanas
                for(int j=0;j<list.size()-1;j++) {
                    yearI = list.get(j).year();
                    yearF = list.get(j + 1).year();
                    mesI = list.get(j).month();
                    mesF = list.get(j + 1).month();
                    diaI = list.get(j).day();
                    diaF = list.get(j + 1).day();
                    if(i<=incomes.size()-1)
                    {
                        yearA = incomes.get(i).getFecha().getYear() + 1900;
                        mesA = incomes.get(i).getFecha().getMonth()+1;
                        diaA = incomes.get(i).getFecha().getDate();
                    }
                    //caso3: cambia de año
                    while( i<= incomes.size()-1&&yearI != yearF && yearI<= yearA &&
                            yearA + 1900<yearF && mesI>=mesA && mesF<=mesA && diaI>diaA && diaA<diaF )
                    {
                        suma+= incomes.get(i).getMonto();
                        i++;
                        if(i<=incomes.size()-1)
                        {
                            yearA = incomes.get(i).getFecha().getYear() + 1900;
                            mesA = incomes.get(i).getFecha().getMonth()+1;
                            diaA = incomes.get(i).getFecha().getDate();
                        }
                    }
                    //caso2.1: el año es el mismo, pero cambia el mes de una semana a la otra tomando
                    // los días antes de finde demes
                    while(i<=incomes.size()-1 && yearI == yearF && mesI<= mesA
                            && mesA<mesF && diaF<diaA)
                    {
                        suma+= incomes.get(i).getMonto();
                        i++;
                        if(i<=incomes.size()-1)
                        {
                            yearA = incomes.get(i).getFecha().getYear() + 1900;
                            mesA = incomes.get(i).getFecha().getMonth()+1;
                            diaA = incomes.get(i).getFecha().getDate();
                        }
                    }
                    //caso 2.2: mismo año pero cambia el mes tomando los dias de comienzo de mes
                    while(i<=incomes.size()-1 && yearI == yearF && mesI< mesA
                            && mesA<=mesF && diaA<diaF)
                    {
                        suma+= incomes.get(i).getMonto();
                        i++;
                        if(i<=incomes.size()-1)
                        {
                            yearA = incomes.get(i).getFecha().getYear() + 1900;
                            mesA = incomes.get(i).getFecha().getMonth()+1;
                            diaA = incomes.get(i).getFecha().getDate();
                        }
                    }
                    //caso 1: no cambia de mes ni de año y solo se toma en cuenta los dias
                    while(i<= incomes.size()-1 && yearI== yearF && mesI==mesF && diaI<= diaA
                            && diaA<diaF ){
                        suma+= incomes.get(i).getMonto();
                        i++;
                        if(i<=incomes.size()-1)
                        {
                            yearA = incomes.get(i).getFecha().getYear() + 1900;
                            mesA = incomes.get(i).getFecha().getMonth()+1;
                            diaA = incomes.get(i).getFecha().getDate();
                        }
                    }
                    double valorRedondeado = Math.round(suma * 100.0) / 100.0;
                    respuesta.add(valorRedondeado);
                    suma=0;
                }
            }else{
                //manipular  dias
                for(int j=0;j<list.size()-1;j++)
                {
                    yearI = list.get(j).year();
                    yearF = list.get(j+1).year();
                    mesI = list.get(j).month();
                    mesF = list.get(j+1).month();
                    diaI = list.get(j).day();
                    diaF = list.get(j+1).day();
                    while( i<=incomes.size()-1&&yearI != yearF && yearI<=incomes.get(i).getFecha().getYear() + 1900 &&
                            incomes.get(i).getFecha().getYear() + 1900<yearF )
                    {
                        suma+= incomes.get(i).getMonto();
                        i++;
                    }
                    while(i<=incomes.size()-1&&yearI == yearF && mesI<=incomes.get(i).getFecha().getMonth()+1
                            && incomes.get(i).getFecha().getMonth()+1<mesF)
                    {
                        suma+=incomes.get(i).getMonto();
                        i++;
                    }
                    while(i<=incomes.size()-1 && yearI== yearF && mesI==mesF && diaI<=incomes.get(i).getFecha().getDate()
                            && incomes.get(i).getFecha().getDate()<diaF ){
                        suma+=incomes.get(i).getMonto();
                        i++;
                    }
                    double valorRedondeado = Math.round(suma * 100.0) / 100.0;
                    respuesta.add(valorRedondeado);
                    suma=0.0;
                }
            }
            return  ResponseEntity.ok(new ListTotalResponse(respuesta,current,"Los valores totales para los graficos"));
        }
    }
}
