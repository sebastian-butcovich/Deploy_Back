package com.example.tryJwt.demo.Services;

import com.example.tryJwt.demo.FileRequest.MovementsRequest;
import com.example.tryJwt.demo.FileRequest.MovementsPagedResponse;
import com.example.tryJwt.demo.Modelo.ActualFlow;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.ActualFlowRepository;
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
    private ActualFlowRepository actualFlowRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired()
    private FunctionUtils functionUtils;

    public ResponseEntity<MovementsPagedResponse> listSpent(Map<String, String> headers)
    {
        int page;
        int page_size;
        try{
             page = Integer.parseInt(headers.get("page"));
             page_size = Integer.parseInt(headers.get("page_size"));
        }catch(Exception e){
            ResponseEntity.badRequest().body("No se envio la cantidad de entradas" + e.getCause());
        }
      Optional<Users> users = functionUtils.getUsers(headers);
        List<ActualFlow> actualFlows =null;
        double montoMin;
        double montoMax;
        String tipo;
        String fecha_inicio;
        String fecha_final;
        if(!Objects.equals(headers.get("monto_min"), "") && !Objects.equals(headers.get("monto_max"), "")
                && headers.get("monto_min") != null && headers.get("monto_max") !=null) {
             montoMin = Double.parseDouble(headers.get("monto_min"));
             montoMax = Double.parseDouble(headers.get("monto_max"));
            if(users.isPresent()){
                actualFlows = actualFlowRepository.findAllByUsuario(users.get().getId(), montoMin, montoMax);
            }

        }
        else if(!Objects.equals(headers.get("tipo"),"") &&  headers.get("tipo") != null)
        {
            tipo = headers.get("tipo");
            actualFlows = actualFlowRepository.findAllByUsuario(users.get().getId(),tipo);
        }
        else if(!Objects.equals(headers.get("fecha_inicio"),"") && !Objects.equals(headers.get("fecha_fin"),"")
                && headers.get("fecha_inicio") !=null && headers.get("fecha_fin") != null)
        {
            fecha_inicio = headers.get("fecha_inicio");
            fecha_final = headers.get("fecha_fin");
            actualFlows = actualFlowRepository.findAllByUsuario(users.get().getId(),fecha_inicio,fecha_final);
        }
        else {
            actualFlows = actualFlowRepository.findAllByUsuario(users.get().getId());
        }
        List<ActualFlow> aux = new ArrayList<>();
        aux.addAll(actualFlows);
        return ResponseEntity.ok().body(functionUtils.armarRespuesta(aux,headers));
    }
    private List<ActualFlow> list(Map<String,String> params)
    {
        Optional<Users> users = functionUtils.getUsers(params);
        List<ActualFlow> actualFlows = actualFlowRepository.findAllByUsuario(users.get().getId());
        return actualFlows;
    }
    @JsonBackReference
    public ResponseEntity<HashSet<String>> obtenerTipos(Map<String,String> params)
    {

       List<ActualFlow> lista  = list(params);
        List<String> retort = new LinkedList<String>();
        for (ActualFlow l:lista)
        {
            retort.add(l.getTipo());
        }
        HashSet<String> result = new HashSet<String>(retort);
        return ResponseEntity.ok(result);
    }
    public ResponseEntity<ActualFlow> obtenerGasto(Integer idSpent)
    {
        if (idSpent== null|| idSpent <=0) {
            return ResponseEntity.badRequest().body(null);
        }
        ActualFlow actualFlow = actualFlowRepository.findById(idSpent).orElseThrow();
        return ResponseEntity.ok().body(actualFlow);
    }
    public ResponseEntity<String> addSpent(MovementsRequest movementsRequest, Map<String,String> headers)
    {
        if(movementsRequest.monto() == null)
        {
            return ResponseEntity.badRequest().body("Uno de los siguientes campos (tipo, descripción, monto) esta vacio o es nulo");
        }
        Date fecha = new Date();
        ActualFlow actualFlow = new ActualFlow();
        actualFlow.setFecha(fecha);
        actualFlow.setDescripcion(movementsRequest.descripcion());
        actualFlow.setMonto(movementsRequest.monto());
        actualFlow.setTipo(movementsRequest.tipo());
        Optional<Users> users = functionUtils.getUsers(headers);
        if(users.isPresent()){
            actualFlow.setUsuario(users.get());
            users.get().setDineroActual(users.get().getDineroActual()- actualFlow.getMonto());
            actualFlowRepository.save(actualFlow);
            userRepository.save(users.get());
            return ResponseEntity.ok().body("Gasto agregado correctamente");
        }else{
            return ResponseEntity.badRequest().body("No se encontro el usuario dueño del gasto");
        }
    }



    public ResponseEntity<String> editSpent(MovementsRequest spent, Map<String,String> params)
    {
        if(spent.tipo() == null || spent.id() == null || spent.id() <= 0)
        {
            return ResponseEntity.badRequest().body("Uno de los siguientes campos (nombre, descripción, monto) esta vacio o es nulo");
        }
        Users users = functionUtils.getUsers(params).orElseThrow();
        ActualFlow f_actualFlow = actualFlowRepository.findById(spent.id()).orElseThrow();
        users.setDineroActual(users.getDineroActual()- f_actualFlow.getMonto()+spent.monto());
        f_actualFlow.setMonto(spent.monto());
        f_actualFlow.setDescripcion(spent.descripcion());
        f_actualFlow.setTipo(spent.tipo());
        f_actualFlow.setFecha(spent.fecha());
        f_actualFlow.setUsuario(users);
        actualFlowRepository.save(f_actualFlow);
        userRepository.save(users);
        return ResponseEntity.ok().body("Gasto editado correctamente");
    }
    public ResponseEntity<String> removeSpent(Map<String,String>params)
    {
        if(params.get("id") == null || Integer.parseInt(params.get("id"))<=0)
        {
            ResponseEntity.badRequest().body("Id ingresado es invalido");
        }else{
            Optional<Users> usuario = functionUtils.getUsers(params);
            if(usuario.isPresent()){
                int idSpent = Integer.parseInt(params.get("id"));
                Optional<ActualFlow> spent = actualFlowRepository.findById(idSpent);
                if(spent.isPresent()) {
                    usuario.get().setDineroActual(usuario.get().getDineroActual()+spent.get().getMonto());
                    actualFlowRepository.deleteById(idSpent);
                    return ResponseEntity.ok().body("Gasto eliminado exitosamente");
                }
            }
        }
        return ResponseEntity.ok().body("El gasto no se encuentra en el sistema");
    }
}
