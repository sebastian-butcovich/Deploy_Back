package com.example.tryJwt.demo.Services;

import com.example.tryJwt.demo.Enums.TipoFutureFlow;
import com.example.tryJwt.demo.FileRequest.FutureFlowDto;
import com.example.tryJwt.demo.FileRequest.FutureFlowPagedResponse;
import com.example.tryJwt.demo.FileRequest.Paginated.InfoPaginated;
import com.example.tryJwt.demo.Mapper.FutureFlowMapper;
import com.example.tryJwt.demo.Modelo.ActualFlow;
import com.example.tryJwt.demo.Modelo.FutureFlow;
import com.example.tryJwt.demo.Modelo.Usuario;
import com.example.tryJwt.demo.Repository.FutureFlowsRespository;
import com.example.tryJwt.demo.Repository.UsuarioRepository;
import com.example.tryJwt.demo.Utils.FunctionUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FutureFlowsService {

    @Autowired
    FunctionUtils functionUtils;

    @Autowired
    FutureFlowMapper futureFlowMapper;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    FutureFlowsRespository futureFlowsRespository;


    public FutureFlowPagedResponse list(String token,
                                          Map<String, String> params) {
        Optional<Usuario> user = functionUtils.getUsers(token);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        Usuario usuario = usuarioRepository.findByEmail(user.get().getEmail()).orElseThrow();
        List<FutureFlow> futureflows = futureFlowsRespository.findFutureFlowsByUsuarioId(usuario.getId());
        InfoPaginated infoPaginated = functionUtils.getinfoPagination(futureflows, params);
        return new FutureFlowPagedResponse(futureflows, infoPaginated.getNext_page(), infoPaginated.getPage(),
                infoPaginated.getPage_size(), infoPaginated.getTotal_entries(), infoPaginated.getTotal_pages(), "OK");
    }

    public FutureFlow get(int id,
                          String token,
                          TipoFutureFlow tipo) {
        Optional<Usuario> username = functionUtils.getUsers(token);
        if (username.isEmpty()) {
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        Optional<FutureFlow> found = futureFlowsRespository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("El " + tipo.toString() + " con id '" + id + "' no encontrado");
        }

        if (!username.get().getId().equals(found.get().getUsuario().getId())) {
            throw new IllegalArgumentException("El " + tipo.toString() + " con id '" + id + "' no pertenece al usuario con id '" + username.get().getId() + "'");
        }
        return found.get();
    }

    @Transactional
    public FutureFlow add(FutureFlowDto ffs,
                              String token,
                              TipoFutureFlow tipo) {
        Optional<Usuario> username = functionUtils.getUsers(token);
        if(username.isEmpty()){
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        /*if(!validateFutureFlowAdd(ffs)){
            throw new IllegalArgumentException("FutureFlow incorrecto");
        }*/
        /*List<FutureFlow> ffList = futureFlowsRespository.findFutureFlowsByUsuarioId(username.get().getId());
        if(searchRepeat(ffList,ffs)) {
            throw new RuntimeException("Datos repetidos");
        }*/
        FutureFlow savedFF = futureFlowMapper.toEntity(ffs);
        savedFF.setFecha(savedFF.getFecha());
        savedFF.setUsuario(username.get());
        savedFF.setTipo(tipo);
        savedFF.setFechaCreacion(new Date());
        savedFF.setFechaUltimaModificacion(new Date());
        if (!FutureFlow.isValid(savedFF)) {
            throw new IllegalArgumentException("El elemento " + tipo.toString().toLowerCase() + " ingresado por parametro no es valido");
        }
        return futureFlowsRespository.save(savedFF);
    }

    // Para mi esto nunca se ejecuta, por las comprobaciones en fechas (siempre obtenes new Date())
    // Hay que darle una vuelta de rosca a esto
    /* private boolean searchRepeat(List<FutureFlow> futureflows, FutureFlow deb) {
        boolean response = false;
        for(FutureFlow futureflow: futureflows){
            if (futureflow.equals(deb)) {
                response = true;
                break;
            }
        }
        return response;
    }*/

    // Redundante. Se hace comprobación desde el model con annotation nullable = false
    /*
    private boolean validateFutureFlowAdd(FutureFlowDto futureflow) {
        return   futureflow.nombreContraparte() != null && !futureflow.nombreContraparte().isEmpty()
                && futureflow.monto() != 0 && futureflow.valorDelDolar() != 0;
    }

    private boolean validateFutureFlowEdit(FutureFlow futureflows) {
        return  futureflows.getId() != 0 && futureflows.getNombreContraparte() != null && !futureflows.getNombreContraparte().isEmpty()
                && futureflows.getMonto() != 0 && futureflows.getValorDelDolar() != 0 && futureflows.getFecha() != null;
    } */

    @Transactional
    public FutureFlow update(int id,
                            FutureFlowDto dto,
                            String token,
                            TipoFutureFlow tipo) {
        Optional<Usuario> username = functionUtils.getUsers(token);
        if(username.isEmpty()){
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        /*if(!validateFutureFlowEdit(futureflows)){
            throw new IllegalArgumentException("FutureFlow incorrecto");
        }*/
        Optional<FutureFlow> found = futureFlowsRespository.findById(id);
        if(found.isEmpty()){
            throw new EntityNotFoundException("El " + tipo.toString() + " con id '" + id + "' no encontrado");
        }
        if(username.get().getId().equals(found.get().getUsuario().getId())) {
            throw new IllegalArgumentException("El " + tipo.toString() + " con id '" + id + "' no pertenece al usuario con id '" + username.get().getId() + "'");
        }
        FutureFlow newFF = futureFlowMapper.toEntity(dto);
        newFF.setId(id);
        newFF.setUsuario(username.get());
        newFF.setFechaCreacion(found.get().getFechaCreacion());
        newFF.setFechaUltimaModificacion(new Date());
        if(!FutureFlow.isValid(newFF)) {
            throw new IllegalArgumentException("El elemento " + tipo.toString().toLowerCase() + " ingresado por parametro no es valido");
        }
        return futureFlowsRespository.save(newFF);
    }

    @Transactional
    public void delete(int id,
                         String token,
                         TipoFutureFlow tipo) {
        Optional<Usuario> username = functionUtils.getUsers(token);
        if(username.isEmpty()){
            throw new EntityNotFoundException("Usuario no encontrado");
        }
        Optional<FutureFlow> found = futureFlowsRespository.findById(id);
        if(found.isEmpty()){
            throw new EntityNotFoundException("El " + tipo.toString() + " con id '" + id + "' no encontrado");
        }
        if(username.get().getId().equals(found.get().getUsuario().getId())) {
            throw new IllegalArgumentException("El " + tipo.toString() + " con id '" + id + "' no pertenece al usuario con id '" + username.get().getId() + "'");
        }
        futureFlowsRespository.deleteById(id);
    }
}
