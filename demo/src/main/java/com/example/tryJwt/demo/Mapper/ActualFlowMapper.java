package com.example.tryJwt.demo.Mapper;

import com.example.tryJwt.demo.FileRequest.Request.MovementsRequest;
import com.example.tryJwt.demo.Modelo.ActualFlow;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActualFlowMapper {
    MovementsRequest toDto(ActualFlow flow);
    ActualFlow toEntity(MovementsRequest dto);
}
