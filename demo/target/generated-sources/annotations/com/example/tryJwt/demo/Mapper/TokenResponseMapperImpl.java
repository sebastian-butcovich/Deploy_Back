package com.example.tryJwt.demo.Mapper;

import com.example.tryJwt.demo.FileRequest.TokenResponse;
import com.example.tryJwt.demo.Modelo.Token;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-14T19:48:56-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Amazon.com Inc.)"
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
