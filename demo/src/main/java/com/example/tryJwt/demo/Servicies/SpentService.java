package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.FileRequest.MovementsRequest;
import com.example.tryJwt.demo.Modelo.Spent;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.SpentRepository;
import com.example.tryJwt.demo.Repository.UserRepository;
import com.example.tryJwt.demo.Utils.FunctionUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SpentService {
    @Autowired
    private SpentRepository spentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired(required = true)
    private FunctionUtils functionUtils;
    public ResponseEntity<Page<Spent>> listSpent(Map<String, String> headers)
    {
        int page = Integer.parseInt(headers.get("page"));
        int page_size = Integer.parseInt(headers.get("page_size"));
        Pageable pageable = PageRequest.of(page,page_size);
        Optional<Users> users = functionUtils.getUsers(headers);
        Page<Spent> spents = spentRepository.findAllByUsuario(users.get().getId(),pageable);
        return ResponseEntity.status(HttpStatus.OK).header("Content-Type","application/json")
                .body(spents) ;
    }
    private List<Spent> list(Map<String,String> params)
    {
        Optional<Users> users = functionUtils.getUsers(params);
        List<Spent> spents = spentRepository.findAllByUsuario(users.get().getId());
        return spents;
    }
    @JsonBackReference
    public ResponseEntity<HashSet<String>> obtenerTipos(Map<String,String> params)
    {
       List<Spent> lista  = list(params);
        List<String> retort = new LinkedList<String>();
        for (Spent l:lista)
        {
            retort.add(l.getTipo());
        }
        HashSet<String> result = new HashSet<String>(retort);
        return ResponseEntity.ok(result);
    }
    public ResponseEntity<Spent> obtenerGasto(Integer idSpent)
    {
        if (idSpent== null|| idSpent <=0) {
            return ResponseEntity.badRequest().body(null);
        }
        Spent spent = spentRepository.findById(idSpent).orElseThrow();
        return ResponseEntity.ok().body(spent);
    }
    public ResponseEntity<String> addSpent(MovementsRequest movementsRequest, Map<String,String> headers)
    {
        if(movementsRequest.monto() == null)
        {
            return ResponseEntity.badRequest().body("Uno de los siguientes campos (tipo, descripción, monto) esta vacio o es nulo");
        }
        Date fecha = new Date();
        Spent spent  = new Spent();
        spent.setFecha(fecha);
        spent.setDescripcion(movementsRequest.descripcion());
        spent.setMonto(movementsRequest.monto());
        spent.setTipo(movementsRequest.tipo());
        Optional<Users> users = functionUtils.getUsers(headers);
        spent.setUsuario(users.get());
        spentRepository.save(spent);
        return ResponseEntity.ok().body("Gasto agregado correctamente");
    }



    public ResponseEntity<String> editSpent(MovementsRequest spent, Map<String,String> params)
    {
        if(spent.tipo() == null || spent.id() == null || spent.id() <= 0)
        {
            return ResponseEntity.badRequest().body("Uno de los siguientes campos (nombre, descripción, monto) esta vacio o es nulo");
        }
        Spent f_spent = spentRepository.findById(spent.id()).orElseThrow();
        f_spent.setMonto(spent.monto());
        f_spent.setDescripcion(spent.descripcion());
        f_spent.setTipo(spent.tipo());
        f_spent.setFecha(new Date());
        Optional<Users> users = functionUtils.getUsers(params);
        f_spent.setUsuario(users.get());
        spentRepository.save(f_spent);
        return ResponseEntity.ok().body("Gasto editado correctamente");
    }
    public ResponseEntity<String> removeSpent(Integer idSpent)
    {
        if(idSpent == null || idSpent <=0)
        {
            ResponseEntity.badRequest().body("Id ingresado es invalido");
        }else{
            Optional<Spent> spent = spentRepository.findById(idSpent);
            if(spent.isPresent()) {
                spentRepository.deleteById(idSpent);
                return ResponseEntity.ok().body("Gasto eliminado exitosamente");
            }
        }
        return ResponseEntity.ok().body("El gasto no se encuentra en el sistema");
    }
}
