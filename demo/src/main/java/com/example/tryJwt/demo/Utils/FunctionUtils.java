package com.example.tryJwt.demo.Utils;

import com.example.tryJwt.demo.FileRequest.ApiDolarResponse;
import com.example.tryJwt.demo.Modelo.Income;
import com.example.tryJwt.demo.Modelo.Spent;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.UserRepository;
import com.example.tryJwt.demo.Servicies.JwtService;
import com.example.tryJwt.demo.Servicies.RequestService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.MathContext;
import java.math.RoundingMode;
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
}
