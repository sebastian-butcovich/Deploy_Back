package com.example.tryJwt.demo.Services;

import com.example.tryJwt.demo.Enums.Roles;
import com.example.tryJwt.demo.FileRequest.UpdateUsers;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.UserRepository;
import com.example.tryJwt.demo.Utils.FunctionUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsersService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FunctionUtils functionUtils;


    public List<Users> list (String token) {
        Optional<Users> username = functionUtils.getUsers(token);
        if(username.isEmpty()){
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        if(!username.get().getRoles().contains(Roles.ADMIN.toString())){
            throw new IllegalArgumentException("No posee el rol necesario para ejecutar la operacion");
        }
        return userRepository.findAll();
    }

    @Transactional
    public Users update(UpdateUsers users, String token) {
        Optional<Users> username = functionUtils.getUsers(token);
        if(username.isEmpty()){
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        if(users.name().isEmpty() || users.email().isEmpty()){
            throw new IllegalArgumentException("No se enviaron todos los datos necesarios. Los datos son: nombre," +
                    " email, contraseña vieja y contraseña nueva");
        }
        Users us = username.get();
        us.setName(users.name());
        us.setEmail(users.email());
        if(users.newPassword() != null && !users.newPassword().isEmpty()) {
            if(!passwordEncoder.matches(users.oldPassword(),username.get().getPassword())){
                throw new IllegalArgumentException("La contraseña anterior no es correcta");
            }
            us.setPassword(passwordEncoder.encode(users.newPassword()));
        }else {
            us.setPassword(username.get().getPassword());
        }
        us.setFoto(users.foto());
        return userRepository.save(us);
    }

    public Users whoAmI(String token, Map<String,String> params) {
        Optional<Users> username = functionUtils.getUsers(token);
        if(username.isEmpty()){
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        Users user = username.get();
       if(params.get("currency") != null && !params.get("currency").isEmpty() &&
               !params.get("currency").equals("ars")){
           double actualAux = user.getDineroActual();
           double moneda;
           if(params.get("currency_type")==null || params.get("currency_type").isEmpty()){
                moneda = functionUtils.getValue(params.get("currency"),"");
           }else{
                moneda = functionUtils.getValue(params.get("currency"),params.get("currency_type"));
           }
           actualAux = Math.round((actualAux/moneda)*100.0)/100.0;
           user.setDineroActual(actualAux);
           return user;
       }
       return user;
    }

    @Transactional
    public void delete(String token) {
        Optional<Users> username = functionUtils.getUsers(token);
        if(username.isEmpty()){
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        userRepository.deleteById(username.get().getId().longValue());
    }

    @Transactional
    public Users actualizarValorActual(String token, Double valorActual) {
        if(valorActual<0.0){
            throw new IllegalArgumentException("El valor que se quiere actualizar el monto actual es invalido");
        }
        Optional<Users> username = functionUtils.getUsers(token);
        if(username.isEmpty()){
            throw new EntityNotFoundException("Usuario no encontrado");
        } else {
            username.get().setDineroActual(valorActual);
            return userRepository.save(username.get());
        }
    }
}
