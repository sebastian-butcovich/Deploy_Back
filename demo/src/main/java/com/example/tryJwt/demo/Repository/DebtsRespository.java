package com.example.tryJwt.demo.Repository;

import com.example.tryJwt.demo.Modelo.FutureFlows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtsRespository extends JpaRepository<FutureFlows,Integer> {
    List<FutureFlows> findDebtsByUsuarioId(Integer id);
}
