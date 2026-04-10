package com.example.tryJwt.demo.FileRequest.Request;

import com.example.tryJwt.demo.Enums.TipoActualFlow;

import java.time.LocalDate;

public record MovementsRequest(Double monto, TipoActualFlow tipo, String subtipo, String descripcion, LocalDate fecha){

}
