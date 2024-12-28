package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.FileRequest.SpentRequest;
import com.example.tryJwt.demo.Modelo.Spent;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.SpentRepository;
import com.example.tryJwt.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SpentService {
    @Autowired
    private SpentRepository spentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    public List<Spent> listSpent(Map<String, String> headers)
    {
        int page = Integer.parseInt(headers.get("page"));
        int page_size = Integer.parseInt(headers.get("page_size"));
         Optional<Users> users = getUsers(headers);
        return spentRepository.findAllByUsuario(users.get());
    }
    public ResponseEntity<Spent> obtenerGasto(Integer idSpent)
    {
        if (idSpent== null|| idSpent <=0) {
            return ResponseEntity.badRequest().body(null);
        }
        Spent spent = spentRepository.findById(idSpent).orElseThrow();
        return ResponseEntity.ok().body(spent);
    }
    public ResponseEntity<String> addSpent(SpentRequest spentRequest, Map<String,String> headers)
    {
        if(spentRequest.monto() == null)
        {
            return ResponseEntity.badRequest().body("Uno de los siguientes campos (tipo, descripción, monto) esta vacio o es nulo");
        }
        Date fecha = new Date();
        Spent spent  = new Spent();
        spent.setFecha(fecha);
        spent.setDescripcion(spentRequest.descripcion());
        spent.setMonto(spentRequest.monto());
        spent.setTipo(spentRequest.tipo());
        Optional<Users> users = getUsers(headers);
        spent.setUsuario(users.get());
        spentRepository.save(spent);
        return ResponseEntity.ok().body("Gasto agregado correctamente");
    }

    private Optional<Users> getUsers(Map<String, String> headers) {
        String token = headers.get("token").substring(7);
        String username = jwtService.extractUsername(token);
        Optional<Users> users = userRepository.findByEmail(username);
        return users;
    }

    public ResponseEntity<String> editSpent(Spent spent)
    {
        if(spent.getTipo() == null || spent.getTipo() ==""
                &&spent.getDescripcion() ==null && spent.getDescripcion()==""
                &&spent.getMonto() ==null && spent.getMonto() <= 0 )
        {
            return ResponseEntity.badRequest().body("Uno de los siguientes campos (nombre, descripción, monto) esta vacio o es nulo");
        }
        spentRepository.save(spent);
        return ResponseEntity.ok().body("Gasto editado correctamente");
    }
    public ResponseEntity<String> removeSpent(Integer idSpent)
    {
        if(idSpent == null || idSpent <=0)
        {
            ResponseEntity.badRequest().body("Id ingresado es invalido");
        }else{
            Optional<Spent> spent = spentRepository.findById(idSpent);
            if(spent.isEmpty()) {
                spentRepository.deleteById(idSpent);
                return ResponseEntity.ok().body("Gasto eliminado exitosamente");
            }
        }
        return ResponseEntity.ok().body("El gasto no se encuentra en el sistema");
    }
}
