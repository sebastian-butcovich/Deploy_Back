package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.FileRequest.MovementsRequest;
import com.example.tryJwt.demo.Modelo.Income;
import com.example.tryJwt.demo.Modelo.Spent;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.IncomeRepository;
import com.example.tryJwt.demo.Utils.FunctionUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@Service
public class IncomeService {
    @Autowired
    private IncomeRepository incomeRepository;
    @Autowired
    private FunctionUtils functionUtils;
    public ResponseEntity<Page<Income>> listarIngresos(Map<String,String> params)
    {
        int page = Integer.parseInt(params.get("page"));
        int page_size = Integer.parseInt(params.get("page_size"));
        Pageable pageable = PageRequest.of(page,page_size);
        Optional<Users> users = functionUtils.getUsers(params);
        Page<Income> incomes = incomeRepository.findAllByUsuario(users.get().getId(),pageable);
        return ResponseEntity.status(HttpStatus.OK).header("Content-Type","application/json")
                .body(incomes) ;
    }
    public ResponseEntity<Income> obtenerUnIngreso(Map<String,String> params)
    {
        return null;
    }
    public ResponseEntity<String> agregarIngreso(MovementsRequest ingreso, Map<String,String> params)
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
        incomeRepository.save(incom);
        return ResponseEntity.ok("Ingreso exitoso");
    }
    public ResponseEntity<String> editarIngreso(MovementsRequest ingreso, Map<String,String> params)
    {
        return null;
    }
    public ResponseEntity<String> eliminarIngreso( Map<String,String> params)
    {
        return null;
    }
    public ResponseEntity<HashSet<String>> obtenerTiposIngreso(Map<String,String> params)
    {
        return null;
    }

}

