package com.example.tryJwt.demo.Mapper;

import com.example.tryJwt.demo.FileRequest.RegisterRequest;
import com.example.tryJwt.demo.Modelo.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegisterRequestMapper {
    RegisterRequest toDto(Users user);
    Users toEntity(RegisterRequest dto);
}
