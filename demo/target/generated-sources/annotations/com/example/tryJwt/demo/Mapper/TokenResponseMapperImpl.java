package com.example.tryJwt.demo.Mapper;

import com.example.tryJwt.demo.FileRequest.Responses.TokenResponse;
import com.example.tryJwt.demo.Modelo.Token;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-24T17:04:41-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25 (Oracle Corporation)"
)
@Component
public class TokenResponseMapperImpl implements TokenResponseMapper {

    @Override
    public Token toEntity(TokenResponse tokenResponse) {
        if ( tokenResponse == null ) {
            return null;
        }

        Token.TokenBuilder token = Token.builder();

        return token.build();
    }

    @Override
    public TokenResponse toDto(Token token) {
        if ( token == null ) {
            return null;
        }

        String accessToken = null;
        String refreshToken = null;

        TokenResponse tokenResponse = new TokenResponse( accessToken, refreshToken );

        return tokenResponse;
    }
}
