package com.example.tryJwt.demo.Mapper;

import com.example.tryJwt.demo.FileRequest.UsuarioDto;
import com.example.tryJwt.demo.Modelo.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioDto toDto(Usuario e);
    Usuario toEntity(UsuarioDto dto);
}
