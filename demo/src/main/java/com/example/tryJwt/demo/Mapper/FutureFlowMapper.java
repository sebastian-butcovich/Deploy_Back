package com.example.tryJwt.demo.Mapper;

import com.example.tryJwt.demo.FileRequest.FutureFlowDto;
import com.example.tryJwt.demo.Modelo.FutureFlow;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FutureFlowMapper {
    FutureFlow toEntity(FutureFlowDto ff);
    FutureFlowDto toDto(FutureFlow ff);
}
