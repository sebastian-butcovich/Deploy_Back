package com.example.tryJwt.demo.Utils;

import com.example.tryJwt.demo.FileRequest.Additional_info;
import com.example.tryJwt.demo.FileRequest.ApiDolarResponse;
import com.example.tryJwt.demo.FileRequest.MovementsRequest;
import com.example.tryJwt.demo.FileRequest.MovementsResponse;
import com.example.tryJwt.demo.FileRequest.Paginated.InfoPaginated;
import com.example.tryJwt.demo.Modelo.Flow;
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
        return userRepository.findByEmail(username);
    }
    public void changeCoins(List<Flow> spents, String current, double value)
    {
        //double value = getValue(current, currentType);
        for(Flow lis:spents)
        {
            double valor = (lis.getMonto()/value);
            double valorRedondeado = Math.round(valor * 100.0) / 100.0;
            lis.setMonto(valorRedondeado);
            lis.setMoneda(current);
        }
    }

    public double getValue(String current, String currentType) {
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
    public MovementsResponse armarRespuesta(List<Flow> ingresos, Map<String,String> headers)
    {
        String cotizacion;
        String tipo_de_cotizacion="";
        if(!headers.containsKey("currency") || headers.get("currency").equals("ars"))
        {
            cotizacion="ars";
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
            double value = getValue(cotizacion,tipo_de_cotizacion);
            changeCoins(ingresos,cotizacion,value);
        }

        List< MovementsRequest> list = new LinkedList<>();
        InfoPaginated infoPaginated = getinfoPagination(ingresos, headers);
      if(!ingresos.isEmpty())
       {
           for(int i=(infoPaginated.getPage()-1)*infoPaginated.getPage_size();i<infoPaginated.getPage_size()
                   *(infoPaginated.getPage()-1)+infoPaginated.getPage_size()&&i<=ingresos.size()-1;i++)
           {
               list.add(new MovementsRequest(ingresos.get(i).getMonto(),ingresos.get(i).getTipo()
                       ,ingresos.get(i).getDescripcion(),ingresos.get(i).getFecha(),ingresos.get(i).getId()));
           }
       }

        return  new MovementsResponse(list,new Additional_info(cotizacion,tipo_de_cotizacion), infoPaginated.getNext_page()
                , infoPaginated.getPage(), infoPaginated.getPage_size(), infoPaginated.getTotal_entries(),infoPaginated.getTotal_pages());
    }
    public InfoPaginated getinfoPagination(List list, Map<String,String> headers)
    {
        InfoPaginated infoPaginated = new InfoPaginated();
        int next_page=0;
        int page = Integer.parseInt(headers.get("page"));
        int page_size = Integer.parseInt(headers.get("page_size"));
        int total_entries=list.size();
        int total_pages;
        if( (list.size() /page_size == 0) || (list.size() ==page_size ))
        {
            total_pages=1;
            next_page=1;
        }else
        {
            if((list.size() % page_size) == 0){
                total_pages = list.size()/page_size;
            }else{
                total_pages = list.size()/page_size +1;
            }
        }
        if(page<total_pages)
        {
            next_page=page+1;
        }
        infoPaginated.setNext_page(next_page);
        infoPaginated.setPage_size(page_size);
        infoPaginated.setTotal_entries(total_entries);
        infoPaginated.setTotal_pages(total_pages);
        infoPaginated.setPage(page);
        return infoPaginated;
    }

}
