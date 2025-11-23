package com.example.tryJwt.demo.Mapper;

import com.example.tryJwt.demo.FileRequest.TokenResponse;
import com.example.tryJwt.demo.Modelo.Token;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenResponseMapper {
    Token toEntity(TokenResponse tokenResponse);
    TokenResponse toDto(Token token);
}
