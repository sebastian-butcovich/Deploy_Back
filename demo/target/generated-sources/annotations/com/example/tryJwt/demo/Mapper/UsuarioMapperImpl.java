package com.example.tryJwt.demo.Mapper;

import com.example.tryJwt.demo.FileRequest.UsuarioDto;
import com.example.tryJwt.demo.Modelo.Usuario;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-24T17:04:40-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25 (Oracle Corporation)"
)
@Component
public class UsuarioMapperImpl implements UsuarioMapper {

    @Override
    public UsuarioDto toDto(Usuario e) {
        if ( e == null ) {
            return null;
        }

        String email = null;
        String password = null;
        String username = null;
        String firstname = null;
        String surname = null;
        String foto = null;
        Double dineroActual = null;

        email = e.getEmail();
        password = e.getPassword();
        username = e.getUsername();
        firstname = e.getFirstname();
        surname = e.getSurname();
        foto = e.getFoto();
        dineroActual = e.getDineroActual();

        UsuarioDto usuarioDto = new UsuarioDto( email, password, username, firstname, surname, foto, dineroActual );

        return usuarioDto;
    }

    @Override
    public Usuario toEntity(UsuarioDto dto) {
        if ( dto == null ) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setFirstname( dto.firstname() );
        usuario.setSurname( dto.surname() );
        usuario.setUsername( dto.username() );
        usuario.setEmail( dto.email() );
        usuario.setPassword( dto.password() );
        usuario.setDineroActual( dto.dineroActual() );
        usuario.setFoto( dto.foto() );

        return usuario;
    }
}
