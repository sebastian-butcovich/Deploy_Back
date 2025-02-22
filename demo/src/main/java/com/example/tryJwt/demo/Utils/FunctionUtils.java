package com.example.tryJwt.demo.Utils;

import com.example.tryJwt.demo.FileRequest.Additional_info;
import com.example.tryJwt.demo.FileRequest.ApiDolarResponse;
import com.example.tryJwt.demo.FileRequest.MovementsRequest;
import com.example.tryJwt.demo.FileRequest.MovementsResponse;
import com.example.tryJwt.demo.Modelo.Income;
import com.example.tryJwt.demo.Modelo.Spent;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.UserRepository;
import com.example.tryJwt.demo.Servicies.JwtService;
import com.example.tryJwt.demo.Servicies.RequestService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@NoArgsConstructor
@AllArgsConstructor
@Service
public class FunctionUtils {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestService requestService;
    public Optional<Users> getUsers(Map<String, String> headers) {
        String token = headers.get("token").substring(7);
        String username = jwtService.extractUsername(token);
        Optional<Users> users = userRepository.findByEmail(username);
        return users;
    }
    public void changeCoinsSpent(List<Spent> spents, String current, String currentType)
    {
        double value = getValue(current, currentType);
        for(Spent lis:spents)
        {
            double valor = (lis.getMonto()/value);
            double valorRedondeado = Math.round(valor * 100.0) / 100.0;
            lis.setMonto(valorRedondeado);
            lis.setMoneda(current);
        }
    }
    public void changeCoinsIncome(List<Income> incomes, String current, String currentType)
    {
        double value = getValue(current, currentType);
        for(Income lis:incomes)
        {
            double valor = (lis.getMonto()/value);
            double valorRedondeado = Math.round(valor * 100.0) / 100.0;
            lis.setMonto(valorRedondeado);
            lis.setMoneda(current);
        }
    }

    private double getValue(String current, String currentType) {
        double value = 0.0;
        List<ApiDolarResponse> apiDolarResponse = requestService.obtenerCoins();
        List<ApiDolarResponse> apiOther = requestService.obtenerOtrasCoins();
        if(current.equals("usd"))
        {
            for(ApiDolarResponse l: apiDolarResponse)
            {
                if(currentType.equals(l.getCasa()))
                {
                    value = l.getVenta();
                    break;
                }
            }
        }else
        {
            current =current.toUpperCase();
            for(ApiDolarResponse l: apiOther)
            {
                if(current.equals(l.getMoneda()))
                {
                    value = l.getVenta();
                    break;
                }
            }
        }
        return value;
    }
    public MovementsResponse armarRespuestaIngreso (List<Income> ingresos, Map<String,String> headers)
    {
        String cotizacion = "";
        String tipo_de_cotizacion="";
        if(!headers.containsKey("currency") || headers.get("currency").equals("args"))
        {
            cotizacion="args";
        }
        else
        {
            if(headers.get("currency").equals("usd") )
            {
                cotizacion="usd";
                tipo_de_cotizacion=headers.get("currency_type");
            }
            else
            {
                cotizacion=headers.get("currency");
            }
            changeCoinsIncome(ingresos,cotizacion,tipo_de_cotizacion);
        }
        int next_page=0;
        int page = Integer.parseInt(headers.get("page"));
        int page_size = Integer.parseInt(headers.get("page_size"));
        int total_entries=ingresos.size();
        int total_pages=0;
        if( ingresos.size() /page_size == 0)
        {
            total_pages=1;
            next_page=1;
        }else
        {
            total_pages = (ingresos.size() /page_size)+1;
        }
        List< MovementsRequest> list = new LinkedList<>();
        if(page<total_pages)
        {
            next_page=page+1;
        }
       if(!ingresos.isEmpty())
       {
           for(int i=(page-1)*page_size;i<page_size*(page-1)+5&&i<=ingresos.size()-1;i++)
           {
               list.add(new MovementsRequest(ingresos.get(i).getMonto(),ingresos.get(i).getTipo()
                       ,ingresos.get(i).getDescripcion(),ingresos.get(i).getFecha(),ingresos.get(i).getId()));
           }
       }

        return  new MovementsResponse(list,new Additional_info(cotizacion,tipo_de_cotizacion),next_page
                ,page,page_size,total_entries,total_pages);
    }

    public MovementsResponse armarRespuestaGasto (List<Spent> spent, Map<String,String> headers)
    {
        String cotizacion = "";
        String tipo_de_cotizacion="";
        if(!headers.containsKey("currency") || headers.get("currency").equals("args"))
        {
            cotizacion="args";
        }
        else
        {
            if(headers.get("currency").equals("usd"))
            {
                cotizacion="usd";
                tipo_de_cotizacion=headers.get("currency_type");
            }
            else
            {
                cotizacion=headers.get("currency");
            }
            changeCoinsSpent(spent,cotizacion,tipo_de_cotizacion);
        }
        int next_page=0;
        int page = Integer.parseInt(headers.get("page"));
        int page_size = Integer.parseInt(headers.get("page_size"));
        int total_entries=spent.size();
        int total_pages=0;
        if( spent.size() /page_size == 0)
        {
            total_pages=1;
            next_page=1;
        }else
        {
            total_pages = (spent.size() /page_size)+1;
        }
        List< MovementsRequest> list = new LinkedList<>();
        if(page<total_pages)
        {
            next_page=page+1;
        }
        if(!spent.isEmpty())
        {
            for(int i=(page-1)*page_size;i<page_size*page+5&&i<=spent.size()-1;i++)
            {
                list.add(new MovementsRequest(spent.get(i).getMonto(),spent.get(i).getTipo()
                        ,spent.get(i).getDescripcion(),spent.get(i).getFecha(),spent.get(i).getId()));
            }
        }
        return  new MovementsResponse(list,new Additional_info(cotizacion,tipo_de_cotizacion),next_page,page
                ,page_size,total_entries,total_pages);
    }
}
