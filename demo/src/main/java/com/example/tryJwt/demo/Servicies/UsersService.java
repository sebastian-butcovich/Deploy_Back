package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.FileRequest.RegisterRequest;
import com.example.tryJwt.demo.FileRequest.UpdateUsers;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.UserRepository;
import org.apache.catalina.User;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    public RegisterRequest quienSoy(String token)
    {
        String jwtToken = token.substring(7);
       String username = jwtService.extractUsername(jwtToken);
       Users user = userRepository.findByEmail(username).orElseThrow();
       return new RegisterRequest(user.getEmail(),"",user.getName(),"");
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
}
