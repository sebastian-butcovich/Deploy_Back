package com.example.tryJwt.demo.Services;

import com.example.tryJwt.demo.Enums.TipoActualFlow;
import com.example.tryJwt.demo.FileRequest.Request.MovementsRequest;
import com.example.tryJwt.demo.FileRequest.Responses.MovementsPagedResponse;
import com.example.tryJwt.demo.Mapper.ActualFlowMapper;
import com.example.tryJwt.demo.Modelo.ActualFlow;
import com.example.tryJwt.demo.Modelo.Usuario;
import com.example.tryJwt.demo.Repository.ActualFlowRepository;
import com.example.tryJwt.demo.Utils.FunctionUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ActualFlowsService {
    @Autowired
    private ActualFlowRepository actualFlowRepository;

    @Autowired
    private FunctionUtils functionUtils;

    @Autowired
    private ActualFlowMapper actualFlowMapper;


    // Reescribir, no me gusta la implementacion
    public MovementsPagedResponse pagedList(Map<String,String> params,
                                                                 String token,
                                                                 TipoActualFlow tipo)
    {
        Optional<Usuario> users = functionUtils.getUsers(token);
        List<ActualFlow> actualFlows;
        double montoMin;
        double montoMax;
        String subtipo;
        String fecha_inicio;
        String fecha_final;
        if(users.isPresent() && !Objects.equals(params.get("monto_min"), "") &&
                !Objects.equals(params.get("monto_max"), "") && params.get("monto_min") != null &&
                params.get("monto_max") !=null) {
            montoMin = Double.parseDouble(params.get("monto_min"));
            montoMax = Double.parseDouble(params.get("monto_max"));
            actualFlows = actualFlowRepository.findAllByUsuario(users.get().getId(), tipo, montoMin, montoMax);

        } else if(users.isPresent() && !Objects.equals(params.get("subtipo"),"") &&
                params.get("subtipo") != null) {
            subtipo = params.get("subtipo");
            actualFlows = actualFlowRepository.findAllByUsuario(users.get().getId(), tipo, subtipo);
        } else if(users.isPresent() && !Objects.equals(params.get("fecha_inicio"),"") &&
                !Objects.equals(params.get("fecha_fin"),"") &&
                params.get("fecha_inicio") !=null && params.get("fecha_fin") != null) {
            fecha_inicio = params.get("fecha_inicio");
            fecha_final = params.get("fecha_fin");
            actualFlows = actualFlowRepository.findAllByUsuario(users.get().getId(), tipo, fecha_inicio, fecha_final);
        } else if(users.isPresent()){
            actualFlows = actualFlowRepository.findAllByUsuario(users.get().getId(), tipo);
        } else {
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        List<ActualFlow> aux = new ArrayList<>(actualFlows);
        return functionUtils.armarRespuesta(aux,params);
    }

    public ActualFlow get(int id,
                              String token,
                              TipoActualFlow tipo) {
        Optional<Usuario> username = functionUtils.getUsers(token);
        if(username.isEmpty()){
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        Optional<ActualFlow> found = actualFlowRepository.findById(id);
        if(found.isEmpty()){
            throw new EntityNotFoundException("El " + tipo.toString() + " con id '" + id + "' no encontrado");
        }

        if(!username.get().getId().equals(found.get().getUsuario().getId())) {
            throw new IllegalArgumentException("El " + tipo.toString() + " con id '" + id + "' no pertenece al usuario con id '" + username.get().getId() + "'");
        }
        return found.get();
    }


    @Transactional
    public ActualFlow add(MovementsRequest movm,
                              String token,
                              TipoActualFlow tipo) {
        Optional<Usuario> username = functionUtils.getUsers(token);
        if(username.isEmpty()){
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        ActualFlow savedMovm = actualFlowMapper.toEntity(movm);
        // savedMovm.setFecha(new Date());
        savedMovm.setFechaCreacion(new Date());
        savedMovm.setFechaUltimaModificacion(new Date());
        savedMovm.setUsuario(username.get());
        savedMovm.setTipo(tipo);
        if (!ActualFlow.isValid(savedMovm)) {
            throw new IllegalArgumentException("El elemento " + tipo.toString().toLowerCase() + " ingresado por parametro no es valido");
        }
        return actualFlowRepository.save(savedMovm);
    }

    @Transactional
    public ActualFlow update(int id,
                             MovementsRequest dto,
                             String token,
                             TipoActualFlow tipo) {
        Optional<Usuario> username = functionUtils.getUsers(token);
        if (username.isEmpty()) {
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        Optional<ActualFlow> found = actualFlowRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("El elemento " + tipo.toString().toLowerCase() + " con id '" + id + "' no encontrado");
        }
        if (username.get().getId().equals(found.get().getUsuario().getId())) {
            throw new IllegalArgumentException("El elemento " + tipo.toString().toLowerCase() + " con id '" + id + "' no pertenece al usuario con id '" + username.get().getId() + "'");
        }
        ActualFlow newAF = actualFlowMapper.toEntity(dto);
        newAF.setId(id);
        newAF.setUsuario(username.get());
        newAF.setFechaCreacion(found.get().getFechaCreacion());
        newAF.setFechaUltimaModificacion(new Date());
        newAF.setTipo(tipo);
        if (!ActualFlow.isValid(newAF)) {
            throw new IllegalArgumentException("El elemento " + tipo.toString().toLowerCase() + " ingresado por parametro no es valido");
        }
        return actualFlowRepository.save(newAF);
    }

    @Transactional
    public void delete(int id,
                         String token,
                         TipoActualFlow tipo) {
        Optional<Usuario> username = functionUtils.getUsers(token);
        if(username.isEmpty()){
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        Optional<ActualFlow> found = actualFlowRepository.findById(id);
        if(found.isEmpty()){
            throw new EntityNotFoundException("El elemento " + tipo.toString().toLowerCase() + " con id '" + id + "' no encontrado");
        }
        if(!username.get().getId().equals(found.get().getUsuario().getId())) {
            throw new IllegalArgumentException("El elemento " + tipo.toString().toLowerCase() + " con id '" + id + "' no pertenece al usuario con id '" + username.get().getId() + "'");
        }
        actualFlowRepository.deleteById(id);
    }


    private List<ActualFlow> list(String token,
                                  TipoActualFlow tipo) {
        Optional<Usuario> users = functionUtils.getUsers(token);
        if (users.isPresent()) {
            return actualFlowRepository.findAllByUsuario(users.get().getId(), tipo);
        } else {
            throw new EntityNotFoundException("Usuario no encontrado");

        }
    }

    public HashSet<String> getAllSubtypes(String token,
                                           TipoActualFlow tipo) {
        List<ActualFlow> list = this.list(token, tipo);
        List<String> retort = new LinkedList<>();
        for (ActualFlow l:list) {
            retort.add(l.getSubtipo());
        }
        return new HashSet<>(retort);
    }

    /*public ResponseEntity<String> editarIngreso(MovementsRequest ingreso, Map<String,String> params)
    {
        if(ingreso.id() == null)
        {
            return ResponseEntity.badRequest().body("No se ingreso ningun id");
        }
        Integer id = ingreso.id();
        Optional<Income> optionalIncome = incomeRepository.findById(id);
        if (optionalIncome.isEmpty())
        {
            return ResponseEntity.badRequest().body("No se encontro ningún ingreso con ese id");
        }
        Income income = optionalIncome.get();
        Users user = functionUtils.getUsers(params).orElseThrow();
        user.setDineroActual(user.getDineroActual()-income.getMonto()+ingreso.monto());
        income.setMonto(ingreso.monto());
        income.setTipo(ingreso.tipo());
        income.setDescripcion(ingreso.descripcion());
        income.setFecha(ingreso.fecha());
        incomeRepository.save(income);
        return ResponseEntity.ok().body("El ingreso se edito correctamente");
    }
    public ResponseEntity<String> eliminarIngreso( Map<String,String> params)
    {
        if(params.get("id").isEmpty())
        {
            return ResponseEntity.badRequest().body("No se envio ningún id");
        }
        Integer id = Integer.parseInt(params.get("id"));
        Optional<Income> optionalIncome = incomeRepository.findById(id);
        if (optionalIncome.isEmpty())
        {
            return ResponseEntity.badRequest().body("No se encontro ningún ingreso con ese id");
        }
        Users users = functionUtils.getUsers(params).orElseThrow();
        users.setDineroActual(users.getDineroActual()-optionalIncome.get().getMonto());
        incomeRepository.deleteById(id);
        userRepository.save(users);
        return ResponseEntity.ok().body("Se elimino el ingreso correctamente");
    }*/
    /*public ResponseEntity<String> agregarIngreso(MovementsRequest ingreso, Map<String,String> params)
    {
        if(ingreso.monto() <= 0 || ingreso.tipo() == null || ingreso.tipo().isEmpty() || ingreso.descripcion() == null
        || ingreso.descripcion().isEmpty())
        {
            return ResponseEntity.badRequest().body("Faltan campos del ingreso. Puede que falte monto, tipo o descripción");
        }
        Income incom = new Income();
        Users users = functionUtils.getUsers(params).orElseThrow();
        incom.setMonto(ingreso.monto());
        incom.setTipo(ingreso.tipo());
        incom.setDescripcion(ingreso.descripcion());
        incom.setUsuario(users);
        incom.setFecha(new Date());
        users.setDineroActual(users.getDineroActual()+incom.getMonto());
        incomeRepository.save(incom);
        userRepository.save(users);
        return ResponseEntity.ok("Ingreso exitoso");
    }*/

}

