package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.FileRequest.DebtsResponse;
import com.example.tryJwt.demo.Modelo.Debts;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.DebtsRespository;
import com.example.tryJwt.demo.Repository.UserRepository;
import com.example.tryJwt.demo.Utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<DebtsResponse>  listarDebts( Map<String,String> params)
    {
        if(!params.containsKey("token"))
        {
            ResponseEntity.badRequest().body(new DebtsResponse(
                    null,null,0,0,0,0,"Token invalido"
            ));
        }
        String email = jwtService.extractUsername(params.get("token"));
        Users users = userRepository.findByEmail(email).orElseThrow();
        List<Debts> debts = debtsRespository.findDebtsByUsuarioId(users.getId());

        return null;
    }
    public ResponseEntity<String> agregarDebts( String token,  Debts debts)
    {
        return null;
    }
    public ResponseEntity<String> editarDebts( String token,  Debts debts)
    {
        return null;
    }
    public ResponseEntity<String> eliminarDebts(String token, Integer id)
        {
        return null;
        }
}
