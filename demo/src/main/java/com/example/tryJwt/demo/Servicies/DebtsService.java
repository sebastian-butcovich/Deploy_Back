package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.FileRequest.DebtsResponse;
import com.example.tryJwt.demo.FileRequest.Paginated.InfoPaginated;
import com.example.tryJwt.demo.Modelo.Debts;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.DebtsRespository;
import com.example.tryJwt.demo.Repository.UserRepository;
import com.example.tryJwt.demo.Utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DebtsService {
    @Autowired
    FunctionUtils functionUtils;
    @Autowired
    JwtService jwtService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DebtsRespository debtsRespository;
    public ResponseEntity<DebtsResponse>  listarDebts(Map<String,String> params)
    {
        if(!params.containsKey("token"))
        {
            ResponseEntity.badRequest().body(new DebtsResponse(
                    null,null,0,0,0,0,"Token invalido"
            ));
        }
        Optional<Users> user = functionUtils.getUsers(params);
        Users users = userRepository.findByEmail(user.get().getEmail()).orElseThrow();
        List<Debts> debts = debtsRespository.findDebtsByUsuarioId(users.getId());
        InfoPaginated infoPaginated = functionUtils.getinfoPagination(debts, params);
        return ResponseEntity.ok(new DebtsResponse(debts, infoPaginated.getNext_page(), infoPaginated.getPage(),
                infoPaginated.getPage_size(), infoPaginated.getTotal_entries(),infoPaginated.getTotal_pages(),"OK"));
    }
    public ResponseEntity<String> agregarDebts( Debts debts, Map<String,String> params)
    {
        if(!params.containsKey("token")){
            return ResponseEntity.badRequest().body("Token invalido");
        }
        Optional<Users> username = functionUtils.getUsers(params);
        if(username.isEmpty()){
           return  ResponseEntity.badRequest().body("Usuario invalido");
        }
        if(!validateDebtAdd(debts)){
            return ResponseEntity.badRequest().body("Datos invalidos");
        }
        List<Debts> debtsList = debtsRespository.findDebtsByUsuarioId(username.get().getId());
        Date date =new Date();
        debts.setFecha(date);
        if(searchRepeat(debtsList,debts))
        {
            return ResponseEntity.badRequest().body("Datos repetidos");
        }
        debts.setUsuario(username.get());
        debtsRespository.save(debts);
        return ResponseEntity.ok("Deuda agregada correctamente") ;
    }
    private boolean searchRepeat(List<Debts> debts, Debts deb)
    {
        boolean response = false;
        for(Debts debt: debts){
            if (debt.equals(deb)) {
                response = true;
                break;
            }
        }
        return response;
    }
    private boolean validateDebtAdd(Debts debts)
    {
        return   debts.getNombre() != null && !debts.getNombre().isEmpty()
                && debts.getMonto() != 0 && debts.getValorDelDolar() != 0;
    }
    private boolean validateDebtEdit(Debts debts)
    {
        return  debts.getId() != 0 && debts.getNombre() != null && !debts.getNombre().isEmpty()
                && debts.getMonto() != 0 && debts.getValorDelDolar() != 0 && debts.getFecha() != null;
    }
    public ResponseEntity<String> editarDebts( Debts debts, Map<String,String> params)
    {
       if(params.get("token").isEmpty()){
           return ResponseEntity.badRequest().body("Token invalido");
       }
       Optional<Users> username = functionUtils.getUsers(params);
       if(username.isEmpty()){
           return ResponseEntity.badRequest().body("Usuario invalido");
       }
       if(!validateDebtEdit(debts)){
           return ResponseEntity.badRequest().body("Datos invalidos");
       }
       Debts debtsS = debtsRespository.findById(debts.getId()).orElseThrow();
       debtsS.setNombre(debts.getNombre());
       debtsS.setMonto(debts.getMonto());
       debtsS.setValorDelDolar(debts.getValorDelDolar());
       debtsS.setFecha(debts.getFecha());
       debtsS.setEstado(debts.isEstado());
       debtsRespository.save(debtsS);
       return ResponseEntity.ok("Deuda editada correctamente");
    }
    public ResponseEntity<String> eliminarDebts(Map<String,String> params)
        {
            if(params.get("token").isEmpty()){
                return ResponseEntity.badRequest().body("Token invalido");
            }
            if(params.get("id").isEmpty()){
                return ResponseEntity.badRequest().body("Id invalido");
            }
            Integer id = Integer.parseInt(params.get("id"));
            Optional<Debts> debts = debtsRespository.findById(id);
            if(debts.isEmpty()){
                return ResponseEntity.badRequest().body("Deuda no encontrada");
            }
            debtsRespository.deleteById(id);
            return ResponseEntity.ok("Deuda eliminada correctamente");
        }
}
