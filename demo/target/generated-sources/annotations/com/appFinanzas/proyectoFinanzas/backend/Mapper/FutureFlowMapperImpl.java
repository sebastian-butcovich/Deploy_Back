package com.appFinanzas.proyectoFinanzas.backend.Mapper;

import com.appFinanzas.proyectoFinanzas.backend.Enums.EstadoPD;
import com.appFinanzas.proyectoFinanzas.backend.Enums.TipoFutureFlow;
import com.appFinanzas.proyectoFinanzas.backend.FileRequest.FutureFlowDto;
import com.appFinanzas.proyectoFinanzas.backend.Modelo.FutureFlow;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-25T17:59:08-0300",
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
        Date fecha = null;
        Date fechaEstimadaDePago = null;

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
