package com.example.tryJwt.demo.Services;

import com.example.tryJwt.demo.Enums.Roles;
import com.example.tryJwt.demo.FileRequest.UsuarioDto;
import com.example.tryJwt.demo.Mapper.UsuarioMapper;
import com.example.tryJwt.demo.Modelo.Usuario;
import com.example.tryJwt.demo.Repository.UsuarioRepository;
import com.example.tryJwt.demo.Utils.FunctionUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioMapper usuarioMapper;
    private final JwtService jwtService;
    private final FunctionUtils functionUtils;


    @Transactional(readOnly = true)
    public Iterable<UsuarioDto> getAll(String token) {
        if (functionUtils.checkIsAdmin(token)) {
            List<Usuario> usuarios = repository.findAll();
            return usuarios
                    .stream()
                    .map(usuarioMapper::toDto) //method reference reemplazo de (e -> mapper.toDto(e))
                    .toList();
        } else {
            throw new AuthorizationDeniedException("No tiene permisos para realizar esta accion");
        }
    }


    @Transactional(readOnly = true)
    public UsuarioDto whoAmI(String token) {
        Usuario me = repository.findByEmail(jwtService.extractEmail(token))
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el elemento con token: " + token));
        return usuarioMapper.toDto(me);
    }

    @Transactional(readOnly = true)
    public UsuarioDto get(String token, Long id) {
        if (functionUtils.checkIsAdmin(token)) {
            Usuario found = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("No se encontro el elemento con token: " + token));
            return usuarioMapper.toDto(found);
        } else {
            throw new AuthorizationDeniedException("No tiene permisos para realizar esta accion");
        }
    }

    @Transactional
    public UsuarioDto update(String token, Long id, UsuarioDto dto) {
        if(functionUtils.checkIsAdmin(token)) {
            Usuario found = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("No se encontro el elemento con token: " + token));
            Usuario updated = usuarioMapper.toEntity(dto);
            updated.setId(found.getId());
            updated.setUltimaModificacion(new Date());
            Usuario res = repository.save(updated);
            return usuarioMapper.toDto(res);
        } else {
            throw new AuthorizationDeniedException("No tiene permisos para realizar esta accion");
        }
    }

    @Transactional
    public UsuarioDto updateMyself(String token, UsuarioDto dto) {
        Usuario me = repository.findByEmail(jwtService.extractEmail(token))
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el elemento con token: " + token));
        Usuario updated = usuarioMapper.toEntity(dto);
        updated.setId(me.getId());
        updated.setUltimaModificacion(new Date());
        Usuario res = repository.save(updated);
        return usuarioMapper.toDto(res);
    }

    @Transactional
    public void delete(String token, Long id) {
        if(functionUtils.checkIsAdmin(token)) {
            Usuario me = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("No se encontro el elemento con token: " + token));
            repository.delete(me);
        } else {
            throw new AuthorizationDeniedException("No tiene permisos para realizar esta accion");
        }
    }

    @Transactional
    public void deleteMyself(String token) {
        Usuario me = repository.findByEmail(jwtService.extractEmail(token))
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el elemento con token: " + token));
        repository.delete(me);
    }

    @Transactional
    public Usuario actualizarValorActual(String token, Double valorActual) {
        if(valorActual<0.0){
            throw new IllegalArgumentException("El valor que se quiere actualizar el monto actual es invalido");
        }
        Optional<Usuario> username = functionUtils.getUsers(token);
        if(username.isEmpty()){
            throw new EntityNotFoundException("Usuario no encontrado");
        } else {
            username.get().setDineroActual(valorActual);
            return repository.save(username.get());
        }
    }
}
