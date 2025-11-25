package com.appFinanzas.proyectoFinanzas.backend.Mapper;

import com.appFinanzas.proyectoFinanzas.backend.FileRequest.RegisterRequest;
import com.appFinanzas.proyectoFinanzas.backend.Modelo.Users;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-25T17:59:08-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25 (Oracle Corporation)"
)
@Component
public class RegisterRequestMapperImpl implements RegisterRequestMapper {

    @Override
    public RegisterRequest toDto(Users user) {
        if ( user == null ) {
            return null;
        }

        String email = null;
        String password = null;
        String name = null;
        String foto = null;
        Double dineroActual = null;

        email = user.getEmail();
        password = user.getPassword();
        name = user.getName();
        foto = user.getFoto();
        dineroActual = user.getDineroActual();

        RegisterRequest registerRequest = new RegisterRequest( email, password, name, foto, dineroActual );

        return registerRequest;
    }

    @Override
    public Users toEntity(RegisterRequest dto) {
        if ( dto == null ) {
            return null;
        }

        Users users = new Users();

        users.setName( dto.name() );
        users.setEmail( dto.email() );
        users.setPassword( dto.password() );
        users.setDineroActual( dto.dineroActual() );
        users.setFoto( dto.foto() );

        return users;
    }
}
