package com.example.tryJwt.demo.FileRequest.Responses;

import com.example.tryJwt.demo.FileRequest.UsuarioDto;
import lombok.Builder;

import java.util.Date;

@Builder(toBuilder = true)
public record LoginResponse(String access_token,
                            String refresh_token,
                            String email,
                            String username,
                            String surname,
                            String firstname,
                            Date create,
                            String foto,
                            Double dineroActual) {
}
