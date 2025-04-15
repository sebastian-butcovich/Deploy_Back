package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.FileRequest.MovementsRequest;
import com.example.tryJwt.demo.FileRequest.MovementsResponse;
import com.example.tryJwt.demo.Modelo.Flow;
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

import java.util.*;

@Service
public class IncomeService {
    @Autowired
    private IncomeRepository incomeRepository;
    @Autowired
    private FunctionUtils functionUtils;
    public ResponseEntity<MovementsResponse> listarIngresos(Map<String,String> params)
    {
        int page = Integer.parseInt(params.get("page"));
        int page_size = Integer.parseInt(params.get("page_size"));
        Optional<Users> users = functionUtils.getUsers(params);
        List<Income> incomes = null;
        double montoMin=0.0;
        double montoMax=0.0;
        String tipo = null;
        String fecha_inicio = null;
        String fecha_final = null;
        if(!Objects.equals(params.get("monto_min"), "") && !Objects.equals(params.get("monto_max"), "")
                && params.get("monto_min") != null && params.get("monto_max") !=null) {
            montoMin = Double.parseDouble(params.get("monto_min"));
            montoMax = Double.parseDouble(params.get("monto_max"));
            incomes = incomeRepository.findAllByUsuario(users.get().getId(), montoMin, montoMax);

        }
        else if(!Objects.equals(params.get("tipo"),"") && params.get("tipo") != null)
        {
            tipo = params.get("tipo");
            incomes = incomeRepository.findAllByUsuario(users.get().getId(),tipo);
        }
        else if(!Objects.equals(params.get("fecha_inicio"),"") && !Objects.equals(params.get("fecha_fin"),"")
                && params.get("fecha_inicio") !=null && params.get("fecha_fin") != null)
        {
            fecha_inicio = params.get("fecha_inicio");
            fecha_final = params.get("fecha_fin");
            incomes = incomeRepository.findAllByUsuario(users.get().getId(),fecha_inicio,fecha_final);
        }
        else {
            incomes = incomeRepository.findAllByUsuario(users.get().getId());
        }
        List<Flow> aux = new ArrayList<>();
        aux.addAll(incomes);
        return ResponseEntity.ok().body(functionUtils.armarRespuesta(aux,params));
    }
    private List<Income> list(Map<String,String> params)
    {
        Optional<Users> users = functionUtils.getUsers(params);
        List<Income> incomes = incomeRepository.findAllByUsuario(users.get().getId());
        return incomes;
    }
    public ResponseEntity<Income> obtenerUnIngreso(Map<String,String> params)
    {
        if(params.get("id").isEmpty())
        {
            ResponseEntity.badRequest().body(null);
        }
        Integer id = Integer.parseInt(params.get("id"));
        Income income = incomeRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(income);
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
        income.setMonto(ingreso.monto());
        income.setTipo(ingreso.tipo());
        income.setDescripcion(ingreso.descripcion());
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
        incomeRepository.deleteById(id);
        return ResponseEntity.ok().body("Se elimino el ingreso correctamente");
    }
    public ResponseEntity<HashSet<String>> obtenerTiposIngreso(Map<String,String> params)
    {
        List<Income> list = this.list(params);
        List<String> retort = new LinkedList<String>();
        for (Income l:list)
        {
            retort.add(l.getTipo());
        }
        HashSet<String> result = new HashSet<String>(retort);
        return ResponseEntity.ok(result);
    }

}

