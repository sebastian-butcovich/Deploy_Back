package com.example.tryJwt.demo.FileRequest;

import com.example.tryJwt.demo.Enums.EstadoPD;
import com.example.tryJwt.demo.Enums.TipoFutureFlow;

import java.time.LocalDate;
import java.util.Date;

public record FutureFlowDto( EstadoPD estado,
                             TipoFutureFlow tipo,
                             String subtipo,
                             double monto,
                             String nombreContraparte,
                             double valorDelDolar,
                             LocalDate fecha,
                             LocalDate fechaEstimadaDePago) {
}
