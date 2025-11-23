package com.example.tryJwt.demo.Repository;

import com.example.tryJwt.demo.Modelo.FutureFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FutureFlowsRespository extends JpaRepository<FutureFlow,Integer> {
    List<FutureFlow> findFutureFlowsByUsuarioId(Integer id);
}
