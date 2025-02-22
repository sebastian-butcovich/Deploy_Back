package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.FileRequest.ApiDolarResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;


@Service
public class RequestService {
    @Autowired
    private RestTemplate restTemplate;

    public List<ApiDolarResponse> obtenerCoins()
    {
        ApiDolarResponse[] dolar =  restTemplate.getForObject("https://dolarapi.com/v1/dolares",ApiDolarResponse[].class);
        assert dolar != null;
        return Arrays.asList(dolar);
    }
    public List<ApiDolarResponse> obtenerOtrasCoins()
    {
        ApiDolarResponse[] other =  restTemplate.getForObject("https://dolarapi.com/v1/cotizaciones",ApiDolarResponse[].class);
        assert other != null;
        return Arrays.asList(other);
    }
}
