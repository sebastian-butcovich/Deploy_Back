package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.FileRequest.MovementsRequest;
import com.example.tryJwt.demo.FileRequest.MovementsResponse;
import com.example.tryJwt.demo.Modelo.Flow;
import com.example.tryJwt.demo.Modelo.Spent;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.SpentRepository;
import com.example.tryJwt.demo.Repository.UserRepository;
import com.example.tryJwt.demo.Utils.FunctionUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ResponseEntity<MovementsResponse> listSpent(Map<String, String> headers)
    {
        int page = Integer.parseInt(headers.get("page"));
        int page_size = Integer.parseInt(headers.get("page_size"));
        Optional<Users> users = functionUtils.getUsers(headers);
        List<Spent> spents = null;
        String where = "";
        double montoMin=0.0;
        double montoMax=0.0;
        String tipo = null;
        String fecha_inicio = null;
        String fecha_final = null;
        if(!Objects.equals(headers.get("monto_min"), "") && !Objects.equals(headers.get("monto_max"), "")
                && headers.get("monto_min") != null && headers.get("monto_max") !=null) {
             montoMin = Double.parseDouble(headers.get("monto_min"));
             montoMax = Double.parseDouble(headers.get("monto_max"));
            spents = spentRepository.findAllByUsuario(users.get().getId(), montoMin, montoMax);

        }
        else if(!Objects.equals(headers.get("tipo"),"") &&  headers.get("tipo") != null)
        {
            tipo = headers.get("tipo");
            spents = spentRepository.findAllByUsuario(users.get().getId(),tipo);
        }
        else if(!Objects.equals(headers.get("fecha_inicio"),"") && !Objects.equals(headers.get("fecha_fin"),"")
                && headers.get("fecha_inicio") !=null && headers.get("fecha_fin") != null)
        {
            fecha_inicio = headers.get("fecha_inicio");
            fecha_final = headers.get("fecha_fin");
            spents = spentRepository.findAllByUsuario(users.get().getId(),fecha_inicio,fecha_final);
        }
        else {
            spents = spentRepository.findAllByUsuario(users.get().getId());
        }
        List<Flow> aux = new ArrayList<>();
        aux.addAll(spents);
        return ResponseEntity.ok().body(functionUtils.armarRespuesta(aux,headers));
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
        f_spent.setFecha(spent.fecha());
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
