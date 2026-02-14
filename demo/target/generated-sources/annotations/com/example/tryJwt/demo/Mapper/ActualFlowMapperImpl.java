package com.example.tryJwt.demo.Mapper;

import com.example.tryJwt.demo.Enums.TipoActualFlow;
import com.example.tryJwt.demo.FileRequest.MovementsRequest;
import com.example.tryJwt.demo.Modelo.ActualFlow;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-14T20:43:07-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25 (Oracle Corporation)"
)
@Component
public class ActualFlowMapperImpl implements ActualFlowMapper {

    @Override
    public MovementsRequest toDto(ActualFlow flow) {
        if ( flow == null ) {
            return null;
        }

        Double monto = null;
        TipoActualFlow tipo = null;
        String subtipo = null;
        String descripcion = null;
        LocalDate fecha = null;

        monto = flow.getMonto();
        tipo = flow.getTipo();
        subtipo = flow.getSubtipo();
        descripcion = flow.getDescripcion();
        fecha = flow.getFecha();

        MovementsRequest movementsRequest = new MovementsRequest( monto, tipo, subtipo, descripcion, fecha );

        return movementsRequest;
    }

    @Override
    public ActualFlow toEntity(MovementsRequest dto) {
        if ( dto == null ) {
            return null;
        }

        ActualFlow actualFlow = new ActualFlow();

        actualFlow.setTipo( dto.tipo() );
        actualFlow.setSubtipo( dto.subtipo() );
        actualFlow.setDescripcion( dto.descripcion() );
        actualFlow.setMonto( dto.monto() );
        actualFlow.setFecha( dto.fecha() );

        return actualFlow;
    }
}
