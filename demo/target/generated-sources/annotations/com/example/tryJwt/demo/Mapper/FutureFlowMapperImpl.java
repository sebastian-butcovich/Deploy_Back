package com.example.tryJwt.demo.Mapper;

import com.example.tryJwt.demo.Enums.EstadoPD;
import com.example.tryJwt.demo.Enums.TipoFutureFlow;
import com.example.tryJwt.demo.FileRequest.FutureFlowDto;
import com.example.tryJwt.demo.Modelo.FutureFlow;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-14T20:45:20-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25 (Oracle Corporation)"
)
@Component
public class FutureFlowMapperImpl implements FutureFlowMapper {

    @Override
    public FutureFlow toEntity(FutureFlowDto ff) {
        if ( ff == null ) {
            return null;
        }

        FutureFlow futureFlow = new FutureFlow();

        futureFlow.setEstado( ff.estado() );
        futureFlow.setTipo( ff.tipo() );
        futureFlow.setSubtipo( ff.subtipo() );
        futureFlow.setMonto( ff.monto() );
        futureFlow.setNombreContraparte( ff.nombreContraparte() );
        futureFlow.setFecha( ff.fecha() );
        futureFlow.setFechaEstimadaDePago( ff.fechaEstimadaDePago() );

        return futureFlow;
    }

    @Override
    public FutureFlowDto toDto(FutureFlow ff) {
        if ( ff == null ) {
            return null;
        }

        EstadoPD estado = null;
        TipoFutureFlow tipo = null;
        String subtipo = null;
        double monto = 0.0d;
        String nombreContraparte = null;
        LocalDate fecha = null;
        LocalDate fechaEstimadaDePago = null;

        estado = ff.getEstado();
        tipo = ff.getTipo();
        subtipo = ff.getSubtipo();
        monto = ff.getMonto();
        nombreContraparte = ff.getNombreContraparte();
        fecha = ff.getFecha();
        fechaEstimadaDePago = ff.getFechaEstimadaDePago();

        double valorDelDolar = 0.0d;

        FutureFlowDto futureFlowDto = new FutureFlowDto( estado, tipo, subtipo, monto, nombreContraparte, valorDelDolar, fecha, fechaEstimadaDePago );

        return futureFlowDto;
    }
}
