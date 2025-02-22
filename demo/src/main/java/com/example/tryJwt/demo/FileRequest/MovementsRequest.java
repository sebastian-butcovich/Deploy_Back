package com.example.tryJwt.demo.FileRequest;

import java.util.Date;

public record MovementsRequest(Double monto, String tipo,String descripcion, Date fecha, Integer id){

}
