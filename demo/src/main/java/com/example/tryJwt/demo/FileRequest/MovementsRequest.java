package com.example.tryJwt.demo.FileRequest;

import java.util.Date;

public record MovementsRequest(Double monto, String descripcion, String tipo, Date fecha, Integer id){

}
