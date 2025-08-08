package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.FileRequest.RegisterRequest;
import com.example.tryJwt.demo.FileRequest.UpdateUsers;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.UserRepository;
import com.example.tryJwt.demo.Utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsersService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FunctionUtils functionUtils;

    public List<Users> listarUsuarios ()
    {
        return userRepository.findAll();
    }

    public ResponseEntity<String> updateUser(UpdateUsers users, Map<String,String> params){
        if(users.name().equals("") || users.email().equals("")){
            return ResponseEntity.badRequest().body("No se enviaron todos los datos necesarios. Los datos son: nombre," +
                    " email, contraseña vieja y contraseña nueva");
        }
        Optional<Users> user1 = userRepository.findByEmail(users.email());
        if(!user1.isPresent())
        {
            return ResponseEntity.badRequest().body("El usuario que se quiere editar no existe");
        }
        Users us = user1.get();
        us.setName(users.name());
        us.setEmail(users.email());
        if(users.newPassword() != null && !users.newPassword().isEmpty())
        {
            if(!passwordEncoder.matches(users.oldPassword(),user1.get().getPassword())){
                return ResponseEntity.badRequest().body("La contraseña anterior no es correcta");
            }
            us.setPassword(passwordEncoder.encode(users.newPassword()));
        }else {
            us.setPassword(user1.get().getPassword());
        }
       us.setFoto(users.foto());
        userRepository.save(us);
        return ResponseEntity.ok().body("Se actualizo el usuario correctamente");
    }
    public RegisterRequest quienSoy(Map<String,String>params)
    {
        if(params.get("token") == null || params.get("token").isEmpty()){
            return new RegisterRequest("","","","",0.0);
        }
        String jwtToken = params.get("token").substring(7);
       String username = jwtService.extractUsername(jwtToken);
       Users user = userRepository.findByEmail(username).orElseThrow();
       if(params.get("currency") != null && !params.get("currency").isEmpty()&& !params.get("currency").equals("ars")){
           double actualAux = user.getDineroActual();
           double moneda;
           if(params.get("currency_type")==null || params.get("currency_type").isEmpty()){
                moneda = functionUtils.getValue(params.get("currency"),"");
           }else{
                moneda = functionUtils.getValue(params.get("currency"),params.get("currency_type"));
           }
           actualAux = Math.round((actualAux/moneda)*100.0)/100.0;
           return new RegisterRequest(user.getEmail(),"",user.getName(),"",actualAux);
       }
       return new RegisterRequest(user.getEmail(),"",user.getName(),"",user.getDineroActual());
    }
    public ResponseEntity<String> deleteUser(String token)
    {
        String jwtToken = token.substring(7);
        String username = jwtService.extractUsername(jwtToken);
        Optional<Users> user = userRepository.findByEmail(username);
        if(user.isPresent())
        {
            long id = user.get().getId();
            userRepository.deleteById(id);
            return ResponseEntity.ok().body("Se elimino el usuario correctamente");
        }
        return ResponseEntity.badRequest().body("No fue posible eliminar el usuario. Seguramente el usuari que quiere" +
                "eliminar no existe");

    }
    public ResponseEntity<String> actualizarValorActual(String token, Double valorActual){
        if(valorActual<0){
            return ResponseEntity.badRequest().body("El valor que se quiere actualizar el monto actual es invalido");
        }
        String jwtToken = token.substring(7);
        String username = jwtService.extractUsername(jwtToken);
        Optional<Users> user = userRepository.findByEmail(username);
        if(user.isPresent())
        {
            user.get().setDineroActual(valorActual);
            userRepository.save(user.get());
            return ResponseEntity.ok("Se actualizo el valor");
        }
        return ResponseEntity.badRequest().body("No se encontro el usuario que se quiere actualizar el valor actual");
    }
}
