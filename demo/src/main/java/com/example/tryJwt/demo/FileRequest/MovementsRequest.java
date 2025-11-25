package com.example.tryJwt.demo.FileRequest;

import com.example.tryJwt.demo.Enums.TipoActualFlow;

import java.util.Date;

public record MovementsRequest(Double monto, TipoActualFlow tipo, String subtipo, String descripcion, Date fecha){

}
