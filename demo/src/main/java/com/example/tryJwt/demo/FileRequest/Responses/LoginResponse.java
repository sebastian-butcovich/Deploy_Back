package com.example.tryJwt.demo.FileRequest.Responses;

import com.example.tryJwt.demo.FileRequest.UsuarioDto;

import java.util.Date;

public record LoginResponse(String access_token,
                            String refresh_token,
                            String email,
                            String username,
                            String surname,
                            String firstname,
                            String lastname,
                            Date create,
                            String foto,
                            Double dineroActual) {
}
