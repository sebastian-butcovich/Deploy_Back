package com.example.tryJwt.demo.FileRequest;

import com.example.tryJwt.demo.Enums.EstadoPD;
import com.example.tryJwt.demo.Enums.TipoFutureFlow;

import java.util.Date;

public record FutureFlowDto( EstadoPD estado,
                             TipoFutureFlow tipo,
                             double monto,
                             String nombreContraparte,
                             double valorDelDolar,
                             Date fecha,
                             Date fechaEstimadaDePago) {
}
